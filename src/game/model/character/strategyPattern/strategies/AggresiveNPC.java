package game.model.character.strategyPattern.strategies;

import game.components.Console;
import game.components.GameMechanics;
import game.components.SharedVariables;
import game.model.character.Character;
import game.model.character.strategyPattern.MomentStrategy;
import game.model.item.Item;
import game.model.item.decoratorPattern.MeleeWeapon;
import game.model.item.decoratorPattern.RangedWeapon;
import game.model.item.decoratorPattern.Weapon;
import game.model.item.decoratorPattern.WeaponDecorator;
import game.model.item.decoratorPattern.enchantments.BurningEnchantment;
import game.model.item.decoratorPattern.enchantments.FreezingEnchantment;
import game.model.item.decoratorPattern.enchantments.FrighteningEnchantment;
import game.model.item.decoratorPattern.enchantments.PacifyingEnchantment;
import game.model.item.decoratorPattern.enchantments.SlayingEnchantment;
import game.views.jpanels.GamePlayScreen;

public class AggresiveNPC implements MomentStrategy{
    
    private Character character;
    private GamePlayScreen gamePlayScreen;
    private boolean isAttackPerformed = false;
    private int playerMomentCount;
    public Object previousMapCellObject = SharedVariables.DEFAULT_CELL_STRING;
    
    public AggresiveNPC(GamePlayScreen gamePlayScreen, Character character) {
        this.gamePlayScreen = gamePlayScreen;
        this.character = character;
    }

    @Override
    public void movePlayer(String message, int fromRowNumber, int fromColNumber, int toRowNumber, int toColNumber) {
        
        if(!(gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber] instanceof Character)){
                        
            Object tempPreviousMapCellObject = previousMapCellObject;
            previousMapCellObject = gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber];             
            gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber] = character;
            gamePlayScreen.currentMap.mapData[fromRowNumber][fromColNumber] = tempPreviousMapCellObject;                          
            Console.printInConsole(message);
            
            if(previousMapCellObject instanceof Item && character.backpack.backpackItems.size() < 10)
                pickItemsFromChest();
                        
            gamePlayScreen.repaintMap(); 
            tryPerformAttackIfAnyNearByMonster();
        }   
        
        else if(gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber] instanceof Character){
            Character besidePlayer = (Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber];
            
            if((besidePlayer.isPlayer() || besidePlayer.getIsFriendlyMonster()) && isAttackPerformed == false)
                attack(toRowNumber, toColNumber);
        }
                    
    }

    @Override
    public void attack(int toRowNumber, int toColNumber) {
        
        int attackPoints = character.attackPoint();
        playerMomentCount--;
        isAttackPerformed = true;
        
        if(attackPoints >= ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).getArmorClass()){
                        
            Weapon weapon;
            if(character.getWeaponObject().getItemType().equalsIgnoreCase("Melee"))
                weapon = new WeaponDecorator(new MeleeWeapon());
            else
                weapon = new WeaponDecorator(new RangedWeapon());
            
            if(character.getWeaponObject() != null && character.getWeaponObject().weaponEnchatments != null)
                for (String enchatment : character.getWeaponObject().weaponEnchatments) {
                    
                    switch(enchatment){
                        
                        case "Freezing":
                            weapon = new FreezingEnchantment(gamePlayScreen, weapon, ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]));
                            break;
                            
                        case "Burning":
                            weapon = new BurningEnchantment(weapon, ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]));
                            break;
                        
                        case "Slaying":  
                            weapon = new SlayingEnchantment(weapon, ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]));
                            break;
                            
                        case "Frightening":
                            weapon = new FrighteningEnchantment(gamePlayScreen, weapon, ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]), character);
                            break;
                            
                        case "Pacifying": 
                            weapon = new PacifyingEnchantment(weapon, ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]), gamePlayScreen);
                            break;
                    }
                    
                }
                        
            int damagePoints = weapon.damagePoints(character);
            if(((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).isPlayer()){
                gamePlayScreen.character.hit(damagePoints);
                Console.printInConsole("   => " + character.getName() + " hitted you with " + damagePoints + " damage points");    
            }
            
            else{
                ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).hit(damagePoints);
                Console.printInConsole("   => " + character.getName() + " hitted a friendly monster (" + ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).getName() + ") with " + damagePoints + " damage points");
            }                
        }
        
        else if(((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).isPlayer())
            Console.printInConsole("   => " + character.getName() + " missed hitting you (" + ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).getArmorClass() + " armor class) with " + attackPoints + " attack points");
        else
            Console.printInConsole("   => " + character.getName() + " missed hitting a friendly monster (" + ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).getName() + " - "+ ((Character) gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber]).getArmorClass() + " armor class) with " + attackPoints + " attack points");
    }

    @Override
    public void pickItemsFromChest() {
                
    	Item item = (Item) previousMapCellObject;                                                    
        if(character.items.containsKey(item.itemType) && character.items.get(item.itemType) != null)
            character.backpack.backpackItems.put(item.itemType, item.itemName);                            
        else    
            character.items.put(item.itemType, item.itemName);
                           
        previousMapCellObject = new String(SharedVariables.DEFAULT_CELL_STRING);  
        Console.printInConsole("   => " + character.getName() + " has collected a " + item.getItemType() + "(" + item.getItemName() + ") from the chest");
        character.draw();
    }

    @Override
    public void playTurn() {
                
        isAttackPerformed = false;
        tryPerformAttackIfAnyNearByMonster();
        
        for(playerMomentCount = 0; playerMomentCount < 3; playerMomentCount++){
            
            int[] characterLocation = GameMechanics.getCharacterPosition(gamePlayScreen.currentMap, character);
            int[] playerLocation = GameMechanics.getPlayerPosition(gamePlayScreen.currentMap);
            
            int horizontalDistance = playerLocation[0] - characterLocation[0];
            int verticalDistance = playerLocation[1] - characterLocation[1];
            
            if((horizontalDistance * horizontalDistance < verticalDistance * verticalDistance) && verticalDistance < 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] - 1].equals(SharedVariables.WALL_STRING)){                
                String message = "   => " + character.getName() + " moving left";
                movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] - 1);
            }
            
            else if((horizontalDistance * horizontalDistance < verticalDistance * verticalDistance) && verticalDistance > 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] + 1].equals(SharedVariables.WALL_STRING)){
                String message = "   => " + character.getName() + " moving right";
                movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] + 1);
            }
                
            else if((horizontalDistance * horizontalDistance > verticalDistance * verticalDistance) && horizontalDistance < 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0] - 1][characterLocation[1]].equals(SharedVariables.WALL_STRING)){
                String message = "   => " + character.getName() + " moving up";
                movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0] - 1), characterLocation[1]);
            }
                
            else if((horizontalDistance * horizontalDistance > verticalDistance * verticalDistance) && horizontalDistance > 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0] + 1][characterLocation[1]].equals(SharedVariables.WALL_STRING)){
                String message = "   => " + character.getName() + " moving down";
                movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0] + 1), characterLocation[1]);
            }
            
            else if(verticalDistance < 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] - 1].equals(SharedVariables.WALL_STRING)){                
                String message = "   => " + character.getName() + " moving left";
                movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] - 1);
            }
            
            else if(verticalDistance > 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] + 1].equals(SharedVariables.WALL_STRING)){
                String message = "   => " + character.getName() + " moving right";
                movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] + 1);
            }
            
            try { Thread.sleep(800); } catch(InterruptedException ignored) {}                           
        }        
        
        gamePlayScreen.isTurnFinished = true;
    }

    @Override
    public void moveToNextMap() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addBorderIfRangedWeapon() {
        // TODO Auto-generated method stub
        
    }

    private void tryPerformAttackIfAnyNearByMonster() {
        
        int playerPosition[] = GameMechanics.getCharacterPosition(gamePlayScreen.currentMap, character);
        
        try{
            
            if(gamePlayScreen.currentMap.mapData[playerPosition[0] - 1][playerPosition[1] + 0] instanceof Character){
                Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] - 1][playerPosition[1] + 0];
                if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                    movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] - 1, playerPosition[1] + 0);
            }
            
            else if(gamePlayScreen.currentMap.mapData[playerPosition[0] + 1][playerPosition[1] + 0] instanceof Character){
                Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] + 1][playerPosition[1] + 0];
                if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                    movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] + 1, playerPosition[1] + 0);
            }
            
            else if(gamePlayScreen.currentMap.mapData[playerPosition[0] + 0][playerPosition[1] + 1] instanceof Character){
                Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] + 0][playerPosition[1] + 1];
                if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                    movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] + 0, playerPosition[1] + 1);
            }
            
            else if(gamePlayScreen.currentMap.mapData[playerPosition[0] + 0][playerPosition[1] - 1] instanceof Character){
                Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] + 0][playerPosition[1] - 1];
                if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                    movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] + 0, playerPosition[1] - 1);
            }      
            
            else if(character.getWeaponObject().itemClass.equals("Ranged")){
                
                if(gamePlayScreen.currentMap.mapData[playerPosition[0] - 1][playerPosition[1] - 1] instanceof Character){
                    Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] - 1][playerPosition[1] - 1];
                    if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                        movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] - 1, playerPosition[1] - 1);
                }
                
                else if(gamePlayScreen.currentMap.mapData[playerPosition[0] + 1][playerPosition[1] + 1] instanceof Character){
                    Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] + 1][playerPosition[1] + 1];
                    if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                        movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] + 1, playerPosition[1] + 1);
                }
                
                else if(gamePlayScreen.currentMap.mapData[playerPosition[0] - 1][playerPosition[1] + 1] instanceof Character){
                    Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] - 1][playerPosition[1] + 1];
                    if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                        movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] - 1, playerPosition[1] + 1);
                }
                
                else if(gamePlayScreen.currentMap.mapData[playerPosition[0] + 1][playerPosition[1] - 1] instanceof Character){
                    Character nearByCharacter = (Character) gamePlayScreen.currentMap.mapData[playerPosition[0] + 1][playerPosition[1] - 1];
                    if(!nearByCharacter.getIsFriendlyMonster() && nearByCharacter.getHitScore() > 0)
                        movePlayer(null, playerPosition[0], playerPosition[1], playerPosition[0] + 1, playerPosition[1] - 1);
                }    
            }
        }
        
        catch(Exception ignored){}
        
        finally{}
    }
}