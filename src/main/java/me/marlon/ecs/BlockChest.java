package me.marlon.ecs;

import me.marlon.game.Inventory;
import me.marlon.game.Item;
import me.marlon.gui.GuiChest;
import me.marlon.gui.GuiManager;
import me.marlon.gui.GuiOrigin;
import org.joml.Vector2f;

public class BlockChest extends Block {
    private EntityManager entities;
    private GuiManager gui;
    private GuiChest guiChest;

    private Inventory inventory;

    public BlockChest(Item item, int x, int y, int z, EntityManager entities, GuiManager gui) {
        super(item, true, x, y, z);
        this.entities = entities;
        this.gui = gui;
        this.guiChest = new GuiChest(gui, GuiOrigin.MID, new Vector2f());

        inventory = new Inventory(19);
        guiChest.setRightInventory(inventory);
    }

    @Override
    public void onUse(int user) {
        guiChest.setLeftInventory(entities.getPlayer(user).inventory);
        gui.getLayer(1).toggle(guiChest);
    }
}
