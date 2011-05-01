package com.gemserk.games.taken;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Contact {

	private static class InternalContact {

		Body body;

		boolean inContact = false;

		Vector2 normal = new Vector2();

		public void setContact(Body body, Vector2 normal) {
			this.body = body;
			this.normal.set(normal);
			this.inContact = true;
		}

		public void unsetContact() {
			this.inContact = false;
			this.body = null;
			this.normal.set(0f, 0f);
		}

	}

	private static int maxContactSize = 5;

	private InternalContact[] contacts = new InternalContact[maxContactSize];

	public Contact() {
		for (int i = 0; i < contacts.length; i++)
			contacts[i] = new InternalContact();
	}

	public void addContact(com.badlogic.gdx.physics.box2d.Contact contact, Body body) {
		for (int i = 0; i < contacts.length; i++) {
			InternalContact c = contacts[i];
			if (c.inContact && c.body != body)
				continue;
			c.setContact(body, contact.getWorldManifold().getNormal());
			return;
		}
	}

	public void removeContact(Body body) {
		for (int i = 0; i < contacts.length; i++) {
			InternalContact c = contacts[i];
			if (!c.inContact)
				continue;
			if (c.body != body)
				continue;
			c.unsetContact();
		}
	}

	public boolean isInContact() {
		for (int i = 0; i < contacts.length; i++) {
			InternalContact c = contacts[i];
			if (c.inContact)
				return true;
		}
		return false;
	}

	public Vector2 getNormal() {
		return contacts[0].normal;
	}

	public Entity getEntity() {
		return (Entity) contacts[0].body.getUserData();
	}

	public Body getBody() {
		return contacts[0].body;
	}

}
