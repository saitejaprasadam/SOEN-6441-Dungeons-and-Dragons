package game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import game.components.ExtensionMethods;
import game.model.character.Character;
import game.model.jaxb.ItemJaxb;

@XmlRootElement(name = "Item")
/**
 * This is an item class which is used to store the items currently used by the
 * character while playing the game. It affects various classes of selected
 * items based on the level of the character.
 * 
 * @author Priyanka A
 * 
 * @version 1.0
 * @since 2/24/2017
 */
public class Item
{

    @XmlElement(name = "itemName")
    public String itemName;
    @XmlElement(name = "itemType")
    public String itemType;
    @XmlElement(name = "itemClass")
    public String itemClass;
    @XmlElement(name = "itemLevel")
    public int itemLevel;

    /** creating an object of class Character */

    Character charObj = null;

    private int armorClass = 0;
    private int count;

    public Item(String name, String type, String itemclass, int level)
    {
        itemName = name;
        itemType = type;
        itemClass = itemclass;
        itemLevel = level;
    }

    public String getItemName()
    {
        return itemName;
    }

    /**
     * Armor Class value should be set based on the armor type chosen. AC is
     * used for almost all the items used.
     */

    public void setInitialArmorClass()
    {
        if (itemClass.equalsIgnoreCase("Light") || itemName.equalsIgnoreCase("Medium"))
        {
            armorClass = armorClass + charObj.getOriginalDexterity();
        }
        else
            armorClass = 14;
    }

    public void calculateLevelCount()
    {
        if (itemLevel <= 4)
        {
            count = 1;
        }
        else if (itemLevel <= 8)
        {
            count = 2;
        }
        else if (itemLevel <= 12)
        {
            count = 3;
        }
        else if (itemLevel <= 16)
        {
            count = 4;
        }
        else
            count = 5;
    }
    
    /**
     * return the modifier
     * @return
     */
    public int getModifier(){
        calculateLevelCount();
        if (itemType.equalsIgnoreCase("Armor"))
        {
            setInitialArmorClass();
            count += armorClass;
        }
        
        return count;
    }
    
    /**
     * @param character sets the character class object
     */
    public void setCharacter(Character character){
        charObj = character;
    }

    /**
     * Instantiates a new map.
     */
    public Item()
    {
    }
    
    public static HashMap<String, ArrayList<Item>> getItems()
    {
        HashMap<String, ArrayList<Item>> hMap = new HashMap<String, ArrayList<Item>>();
        
        String[] items = ExtensionMethods.getItemsList();
        for (String item: items)
        {
            Item i = ItemJaxb.getItemFromXml(item);
            if (hMap.containsKey(i.itemType)){
                ArrayList<Item> hMapItemList = hMap.get(i.itemType);
                hMapItemList.add(i);
            }else{
                ArrayList<Item> hMapItemList = new ArrayList<Item>();
                hMap.put(i.itemType, hMapItemList);
                hMapItemList.add(i);
            }
        }
        return hMap;
    }
    
    public static ArrayList<Item> getItems(String itemType, int level)
    {
        HashMap<String, ArrayList<Item>> hMap = getItems();
        
        if (!hMap.containsKey(itemType)){
            // no item found for this item type
            return new ArrayList<Item>();
        }
        
        ArrayList<Item> hMapList = hMap.get(itemType);
        Iterator<Item> hMapListIterator = hMapList.iterator();
        while(hMapListIterator.hasNext()){
            Item i = hMapListIterator.next();
            if (i.itemLevel > level){
                hMapListIterator.remove();
            }
        }
        return hMapList;
    }    
}