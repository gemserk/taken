package com.gemserk.games.floatingislands;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.gemserk.games.floatingislands.LibgdxGame;

public class MainAndroidApplication extends AndroidApplication  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new LibgdxGame(), false);
	}

}
