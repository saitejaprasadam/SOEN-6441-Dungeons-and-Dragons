package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import tests.game.model.map.MapTest;
import tests.game.model.GamePlayTest;
import tests.game.model.campaign.TestCampaignName;
import tests.game.model.campaign.TestDeleteFileOperations;
import tests.game.model.campaign.TestSaveFileOperations;
import tests.game.model.character.AbilityScoresTest;
import tests.game.model.character.ArmorClassTest;
import tests.game.model.character.AttackModifiersTest;
import tests.game.model.character.CombatAttackRollTest;
import tests.game.model.character.DamageModifiersTest;
import tests.game.model.character.StrategyTesting;
import tests.game.model.item.DeleteFileTest;
import tests.game.model.item.FileExistsTest;
import tests.game.model.item.WeaponEnchatmentTest;

/**
 * This class runs all the test cases classes.
 * @author supreet (s_supree)
 */
@RunWith(Suite.class)

@SuiteClasses(
{ ArmorClassTest.class, GamePlayTest.class, MapTest.class, TestCampaignName.class,
  TestSaveFileOperations.class, TestDeleteFileOperations.class, DeleteFileTest.class, FileExistsTest.class, 
  AbilityScoresTest.class, AttackModifiersTest.class, DamageModifiersTest.class, WeaponEnchatmentTest.class,
  CombatAttackRollTest.class, StrategyTesting.class, GamePlayTest.class })

/**
 * This class initiates the junit test
 * @author supreet
 */
public class GameTestRunner
{
    //DrawTest.class, ArmorClassTest.class, ClassIntegrityTest.class, RollTest.class
}
