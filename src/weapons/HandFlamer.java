package weapons;


import java.io.IOException;

import com.jme3.math.ColorRGBA;

import necromunda.Necromunda;

public class HandFlamer extends Flamer {
	public HandFlamer() {
		setName("Hand Flamer");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(15);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new HandFlamerAmmunition());		
	}

	private class HandFlamerAmmunition extends FlamerAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5481499472820080535L;

		public HandFlamerAmmunition() {
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(0);
			setRangeLongLowerBound(0);
			setRangeLongUpperBound(0);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
		}

		@Override
		public void trigger() {
			setBroken(true);
			Necromunda.appendToStatusMessage(getOutOfFuelMessage());
		}
	}
}
