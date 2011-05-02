package com.gemserk.games.taken;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.gemserk.games.taken.LibgdxGame;

public class MainAndroidApplication extends AndroidApplication  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new LibgdxGame(), false);
	}

}
