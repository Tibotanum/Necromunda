package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.Utils;

public class ToxBombAmmunition extends Ammunition {
    /**
	 * 
	 */
    private static final long serialVersionUID = -8643038092601488833L;

    public ToxBombAmmunition(RangeCombatWeapon weapon) {
        super(weapon);
        setName("Tox Bombs");
        setStrength(4);
        setDamage(1);
        setArmorSaveModification(0);
        setRangeShortLowerBound(0);
        setRangeShortUpperBound(0);
        setRangeLongLowerBound(0);
        setRangeLongUpperBound(0);
        setHitRollModificationShort(0);
        setHitRollModificationLong(0);
        setAmmoRoll(0);
        setCost(0);

        setTemplated(true);
        setTemplatePersistent(true);
        setTemplateRadius(1.5f);

        activate(Utils.rollD6());
    }
    
    private void activate(int hitRoll) {
        if (hitRoll >= 4) {
            setStrength(4);
            setDamage(1);
        }
        else {
            setStrength(0);
            setDamage(0);
        }
    }

    @Override
    public boolean isTemplateToBeRemoved() {
        return false;
    }

}
