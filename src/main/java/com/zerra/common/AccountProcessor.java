package com.zerra.common;

import com.playfab.PlayFabClientModels;
import com.playfab.PlayFabSettings;
import com.zerra.common.util.JsonWrapper;

public class AccountProcessor
{

	//TODO: make methods for getting other player data

	private String id;
	
	public AccountProcessor(String id) {
		this.id = id;
		
		JsonWrapper data = new JsonWrapper("data.json");
			PlayFabSettings.TitleId = data.getString("databaseID");
		data.close();
	}
	

	public void process() {
		//TODO: process the account data
		PlayFabClientModels.LoginWithCustomIDRequest request = new PlayFabClientModels.LoginWithCustomIDRequest();
		request.CustomId = id;
		
		// Below, check whether or not the account has purchased the game. If false (!isValidPurchase), exit the game.
		// This is dummy code. Change it as you see fit!
		boolean isValidPurchase = true;
		
		if(!isValidPurchase) {
			System.exit(CrashCodes.INVALID_USER);
		}
	}
}
