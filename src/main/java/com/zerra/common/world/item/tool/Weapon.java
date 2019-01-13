package com.zerra.common.world.item.tool;

/**
 * An interface for melee weapons.
 * 
 * @author Arpaesis
 */
public interface Weapon extends Tool
{

	/**
	 * @return The damage the weapon deals. Note, this is combined with the base
	 *         damage of the user.
	 */
	public float getDamage();
	
	public Weapon setDamage(float damage);
}
