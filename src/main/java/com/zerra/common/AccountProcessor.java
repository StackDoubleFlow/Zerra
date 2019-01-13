package com.zerra.common;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.FutureTask;

import com.playfab.PlayFabClientAPI;
import com.playfab.PlayFabClientModels;
import com.playfab.PlayFabClientModels.GetPlayerProfileRequest;
import com.playfab.PlayFabClientModels.GetPlayerProfileResult;
import com.playfab.PlayFabClientModels.LoginResult;
import com.playfab.PlayFabClientModels.LoginWithCustomIDRequest;
import com.playfab.PlayFabClientModels.PlayerProfileViewConstraints;
import com.playfab.PlayFabClientModels.UserDataRecord;
import com.playfab.PlayFabErrors.PlayFabResult;
import com.playfab.PlayFabSettings;
import com.zerra.common.util.JsonWrapper;

public class AccountProcessor {
	private String id;

	public AccountProcessor(String id) {
		this.id = id;

		JsonWrapper data = new JsonWrapper("data.json", true);
		PlayFabSettings.TitleId = data.getString("databaseID");

		data.close();
	}

	/**
	 * Process the account info here.
	 */
	public void process() {
		boolean isValidPurchase = true;

		LoginWithCustomIDRequest loginRequest = new PlayFabClientModels.LoginWithCustomIDRequest();

		loginRequest.CustomId = id;
		loginRequest.InfoRequestParameters.GetUserData = true;

		FutureTask<PlayFabResult<LoginResult>> loginTask = PlayFabClientAPI.LoginWithCustomIDAsync(loginRequest);

		loginTask.run();

		try {
			PlayFabResult<LoginResult> loginResult = loginTask.get();

			if (loginResult != null) {
				GetPlayerProfileRequest getProfileRequest = new PlayFabClientModels.GetPlayerProfileRequest();

				PlayerProfileViewConstraints profileConstraints = new PlayerProfileViewConstraints();

				getProfileRequest.PlayFabId = loginResult.Result.PlayFabId;

				profileConstraints.ShowDisplayName = true;
				profileConstraints.ShowAvatarUrl = true;
				profileConstraints.ShowLastLogin = true;

				getProfileRequest.ProfileConstraints = profileConstraints;

				FutureTask<PlayFabResult<GetPlayerProfileResult>> profileTask = PlayFabClientAPI.GetPlayerProfileAsync(getProfileRequest);

				try {
					PlayFabResult<GetPlayerProfileResult> profileResult = profileTask.get();

					// display name is here
					String displayName = profileResult.Result.PlayerProfile.DisplayName;
					String avatarURL = profileResult.Result.PlayerProfile.AvatarUrl;
					Date lastLogin = profileResult.Result.PlayerProfile.LastLogin;

					Map<String, UserDataRecord> userData = loginResult.Result.InfoResultPayload.UserData;

					UserDataRecord IsValidPurchase = userData.get("isValidPurchase");

					isValidPurchase = IsValidPurchase.Value == "true";
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}

		if (isValidPurchase == false) {
			System.exit(1);
		}
	}
}
