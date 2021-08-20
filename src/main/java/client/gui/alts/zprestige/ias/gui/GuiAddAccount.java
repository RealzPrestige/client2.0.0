package client.gui.alts.zprestige.ias.gui;

import client.gui.alts.tools.alt.AltDatabase;
import client.gui.alts.zprestige.ias.account.ExtendedAccountData;

public class GuiAddAccount extends AbstractAccountGui {

	public GuiAddAccount()
	{
		super("Add account");
	}

	@Override
	public void complete()
	{
		AltDatabase.getInstance().getAlts().add(new ExtendedAccountData(getUsername(), getPassword(), getUsername()));
	}
}
