package game.model.character.strategyPattern.strategies;

import game.components.Console;
import game.components.GameMechanics;
import game.components.SharedVariables;
import game.model.character.Character;
import game.model.character.strategyPattern.MomentStrategy;
import game.views.jpanels.GamePlayScreen;

/**
 * This class is for  player that is frightened in nature by the sword that is frightening effect
 * @author teja
 * @version 1.0.0
 */
public class FrighteningStrategy implements MomentStrategy{

    private GamePlayScreen gamePlayScreen;
    private Character character, frightenedByCharacter;
    private int frightenedTurns;
    public Object previousMapCellObject = SharedVariables.DEFAULT_CELL_STRING;
    

    /**
     * Default constructor of computer Player
     * @param gamePlayScreen screen play
     * @param character  defender
     * @param frightenedByCharacter attacker
     * @param frightenedTurns turn count
     */
    public FrighteningStrategy(GamePlayScreen gamePlayScreen, Character character, Character frightenedByCharacter, int frightenedTurns) {
        this.gamePlayScreen = gamePlayScreen;
        this.character = character;
        this.frightenedByCharacter = frightenedByCharacter;
        this.frightenedTurns = frightenedTurns;
    }
    
    /**
     * This method moves the player according the action performed
     * @param message action performed by the character
     * @param fromRowNumber initial row position
     * @param fromColNumber initial col position
     * @param toRowNumber  final row position
     * @param toColNumber final col position
     */
    @Override
    public void movePlayer(String message, int fromRowNumber, int fromColNumber, int toRowNumber, int toColNumber) {
        
        if(!(gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber] instanceof Character)){            
            Object tempPreviousMapCellObject = previousMapCellObject;
            previousMapCellObject = gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber];             
            gamePlayScreen.currentMap.mapData[toRowNumber][toColNumber] = character;
            gamePlayScreen.currentMap.mapData[fromRowNumber][fromColNumber] = tempPreviousMapCellObject;                          
            Console.printInConsole(message);                        
            gamePlayScreen.repaintMap(); 
        }          
    }

    /**
     * This method is for attacking character 
     * @param toRowNumber  final row position
     * @param toColNumber final col position
     */
    @Override
    public void attack(int toRowNumber, int toColNumber) {
        // TODO Auto-generated method stub
        
    }

    /**
     * This method pick Items From Chest
     */
    @Override
    public void pickItemsFromChest() {
        // TODO Auto-generated method stub
        
    }

    /**
     * This method is for playing respective turn
     */
    @Override
    public void playTurn() {
        
        if(frightenedTurns > 0){
            
            for(int index = 0; index < 3; index++){
                
                int[] characterLocation = GameMechanics.getCharacterPosition(gamePlayScreen.currentMap, character);
                int[] playerLocation;
                                
                if(frightenedByCharacter.isPlayer())
                    playerLocation = GameMechanics.getPlayerPosition(gamePlayScreen.currentMap);
                else
                    playerLocation = GameMechanics.getCharacterPosition(gamePlayScreen.currentMap, frightenedByCharacter);
                
                int verticalDistance = playerLocation[0] - characterLocation[0];
                int horizontalDistance = playerLocation[1] - characterLocation[1];               
                
                if((horizontalDistance * horizontalDistance < verticalDistance * verticalDistance) && verticalDistance < 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] + 1].equals(SharedVariables.WALL_STRING)){                
                    String message = "   => " + character.getName() + " is frightened by " + frightenedByCharacter.getName() + " and moving right";
                    movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] + 1);
                }
                
                else if((horizontalDistance * horizontalDistance < verticalDistance * verticalDistance) && verticalDistance > 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] - 1].equals(SharedVariables.WALL_STRING)){
                    String message = "   => " + character.getName() + " is frightened by " + frightenedByCharacter.getName() + " and moving left";
                    movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] - 1);
                }
                 
                else if((horizontalDistance * horizontalDistance >= verticalDistance * verticalDistance) && verticalDistance >= 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0] - 1][characterLocation[1]].equals(SharedVariables.WALL_STRING)){
                    String message = "   => " + character.getName() + " is frightened by " + frightenedByCharacter.getName() + " and moving up";
                    movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0] - 1), characterLocation[1]);
                }
                
                else if((horizontalDistance * horizontalDistance >= verticalDistance * verticalDistance) && horizontalDistance <= 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0] + 1][characterLocation[1]].equals(SharedVariables.WALL_STRING)){
                    String message = "   => " + character.getName() + " is frightened by " + frightenedByCharacter.getName() + " and moving down";
                    movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0] + 1), characterLocation[1]);
                }
                
                else if(verticalDistance < 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] - 1].equals(SharedVariables.WALL_STRING)){                
                    String message = "   => " + character.getName() + " is frightened by " + frightenedByCharacter.getName() + " and moving right";
                    movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] + 1);
                }
                
                else if(verticalDistance > 0 && !gamePlayScreen.currentMap.mapData[characterLocation[0]][characterLocation[1] + 1].equals(SharedVariables.WALL_STRING)){
                    String message = "   => " + character.getName() + " is frightened by " + frightenedByCharacter.getName() + " and moving left";
                    movePlayer(message, characterLocation[0], characterLocation[1], (characterLocation[0]), characterLocation[1] - 1);
                }
                
                try { Thread.sleep(800); } catch(InterruptedException ignored) {}                           
            }      
            
            frightenedTurns--;
            
            if(frightenedTurns == 0)
                character.popMomentStrategy();
        }
        
        gamePlayScreen.isTurnFinished = true;
        
    }

    /**
     * This method changes the map if the player completes the current map
     */
    @Override
    public void moveToNextMap() {
        // TODO Auto-generated method stub
        
    }

    /**
     * This method adds Border If Ranged Weapon
     */
    @Override
    public void addBorderIfRangedWeapon() {
        // TODO Auto-generated method stub
        
    }

}