package client.gui.alts.zprestige.ias.gui;

import client.gui.alts.tools.alt.AccountData;
import client.gui.alts.tools.alt.AltDatabase;
import client.gui.alts.zprestige.ias.account.ExtendedAccountData;
import client.gui.alts.zprestige.ias.enums.EnumBool;
import client.gui.alts.zprestige.ias.tools.JavaTools;
import client.gui.alts.zprestige.iasencrypt.EncryptionTools;

class GuiEditAccount extends AbstractAccountGui {
	private final ExtendedAccountData data;
	private final int selectedIndex;

	public GuiEditAccount(int index){
		super("Edit account");
		this.selectedIndex=index;
		AccountData data = AltDatabase.getInstance().getAlts().get(index);

		if(data instanceof ExtendedAccountData){
			this.data = (ExtendedAccountData) data;
		}else{
			this.data = new ExtendedAccountData(data.user, data.pass, data.alias, 0, JavaTools.getJavaCompat().getDate(), EnumBool.UNKNOWN);
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		setUsername(EncryptionTools.decode(data.user));
		setPassword(EncryptionTools.decode(data.pass));
	}

	@Override
	public void complete()
	{
		AltDatabase.getInstance().getAlts().set(selectedIndex, new ExtendedAccountData(getUsername(), getPassword(), hasUserChanged ? getUsername() : data.alias, data.useCount, data.lastused, data.premium));
	}

}
