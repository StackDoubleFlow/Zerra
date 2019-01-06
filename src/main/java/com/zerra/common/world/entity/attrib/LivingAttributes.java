package com.zerra.common.world.entity.attrib;

/**
 * A common class for the attributes of living entities.
 * 
 * @author Arpaesis
 */
public class LivingAttributes
{

	private Attribute HEALTH = Attribute.HEALTH;
	private Attribute STAMINA = Attribute.STAMINA;
	private Attribute MANA = Attribute.MANA;
	private Attribute PHYS_ARMOR = Attribute.PHYS_ARMOR;
	private Attribute SPELL_ARMOR = Attribute.SPELL_ARMOR;

	public Attribute getHealth()
	{
		return HEALTH;
	}

	public Attribute getStamina()
	{
		return STAMINA;
	}

	public Attribute getMana()
	{
		return MANA;
	}

	public Attribute getPhysicalArmor()
	{
		return PHYS_ARMOR;
	}

	public Attribute getSpellArmor()
	{
		return SPELL_ARMOR;
	}
}
