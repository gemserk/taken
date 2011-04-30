package com.gemserk.games.taken;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;

public class Contact {

	public Vector2 normal = new Vector2();

	public boolean inContact = false;
	
	public Entity entity;

	public void setBox2dContact(com.badlogic.gdx.physics.box2d.Contact contact, Entity entity) {
		this.entity = entity;
		this.normal.set(contact.getWorldManifold().getNormal());
		// other info...
		this.inContact = true;
	}

	public void removeBox2dContact() {
		this.inContact = false;
		this.entity = null;
	}

}
