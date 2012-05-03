package weapons;

import necromunda.Necromunda;
import necromunda.Utils;
import weapons.RangeCombatWeapon.RangeCombatWeaponType;

import com.jme3.math.ColorRGBA;

public class MissileLauncher extends RangeCombatWeapon {
	public MissileLauncher() {
		setName("Missile Launcher");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setMoveOrFire(true);	
		setCost(185);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new SuperKrakMissileAmmunition());
		getAmmunitions().add(new FragMissileAmmunition());
	}
	
	private class FragMissileAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2843206583776734477L;

		public FragMissileAmmunition() {
			setName("Frag Missiles");
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(72);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(0);
			setCost(35);
			
			setTemplated(true);
			setTemplateRadius(1.5f);
			
			setScattering(true);
		}
		
		@Override
		public void sustainMalfunction() {
			setBroken(true);
			Necromunda.appendToStatusMessage(String.format("Your %s have run out and your %s cannot be fired anymore.", getName(), getWeapon().getName()));
		}
	}
	
	private class SuperKrakMissileAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 414280688889729893L;

		public SuperKrakMissileAmmunition() {
			setName("Super Krak Missiles");
			setStrength(8);
			setDamage(1);
			setArmorSaveModification(-6);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(72);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(0);
			setCost(115);
			
			setScattering(true);
		}
		
		@Override
		public String getDamageText() {
			return "D6";
		}
		
		@Override
		public int getDamage() {
			int damage = Utils.rollD6();

			return damage;
		}
		
		@Override
		public void sustainMalfunction() {
			setBroken(true);
			Necromunda.appendToStatusMessage(String.format("Your %s have run out and your %s cannot be fired anymore.", getName(), getWeapon().getName()));
		}
	}
}
