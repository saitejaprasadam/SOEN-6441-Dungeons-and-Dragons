package tests.game.model.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import game.components.SharedVariables;
import game.model.Map;
import game.model.character.Character;
import game.model.jaxb.MapJaxb;

/**
 * This class is used to test map model class
 * 
 * @author saiteja prasadam
 * @version 1.0.0
 * @since 2/28/2017
 */
public class MapTest
{

    private static Map mapObject;

    /**
     * This static method initalises map object variable to perform test
     * operations
     */
    @BeforeClass
    public static void initVariables()
    {
        mapObject = new Map("Montreal", 20, 20, null);
    }
    
    @Test
    public void testMap(){
        
        Map map = MapJaxb.getMapFromXml("Montreal");
        map.initalizeMapData("saitej");
        boolean exitDoor = false;
        boolean entryDoor = false;
        boolean objective = false;
                
        for(int i = 0; i < map.mapHeight; i++)
            for(int j = 0; j < map.mapWidth; j++){
                
                if(map.mapCellValues[i][j].equals(SharedVariables.ENTRY_DOOR_STRING)) 
                    entryDoor = true;
                
                else if(map.mapData[i][j] instanceof String && ((String) map.mapData[i][j]).equals(SharedVariables.EXIT_DOOR_STRING))
                    exitDoor = true;
                
                else if(map.mapData[i][j] instanceof String && ((String) map.mapData[i][j]).equals(SharedVariables.KEY_STRING))
                    objective = true;
                
                else if(map.mapData[i][j] instanceof Character && !((Character) map.mapData[i][j]).getIsFriendlyMonster())
                    objective = true;
            }
                
        assertTrue(entryDoor);
        assertTrue(exitDoor);
        assertTrue(objective);
    }

    /**
     * This method test map name
     */
    @Test
    public void testMapName()
    {
        assertEquals(mapObject.getMapName(), "Montreal");
    }

    /**
     * This method test map width
     */
    @Test
    public void testMapWidth()
    {
        assertEquals(mapObject.mapWidth, 20);
    }

    /**
     * This method test map height
     */
    @Test
    public void testMapHeight()
    {
        assertEquals(mapObject.mapHeight, 20);
    }

}
