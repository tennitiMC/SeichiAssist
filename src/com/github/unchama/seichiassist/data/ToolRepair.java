package com.github.unchama.seichiassist.data;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.unchama.seichiassist.util.ExperienceManager;

public class ToolRepair {
	public enum RepairType{
		Free,		//消費なし
		Mending,	//修繕エンチャントを持つアイテムに対して所持経験値で修理
	}

	//プレイヤーの手持ちから全て修繕
	static public void RepairTool(Player player, RepairType type){
		PlayerInventory playerInventory = player.getInventory();
		player.sendMessage("RepairTool(Player player, RepairType type)");
		for(ItemStack item : playerInventory.getContents() ){
			if(item == null){
				continue;
			}
			player.sendMessage("playerInventory.getContents()");
			RepairTool(player, item, type);
		}
	}

	//指定のインベントリ内を全て修繕
	static public void RepairTool(Player player, Inventory inventory, RepairType type){
		for(ItemStack item : inventory.getContents() ){
			if(item == null){
				continue;
			}
			RepairTool(player, item, type);
		}
	}

	//アイテム単体の修繕
	static public void RepairTool(Player player, ItemStack item, RepairType type){
		switch(type){
		case Free:
				Repair(item, (short) 0);
			break;

		case Mending:
			player.sendMessage(item.getType().name() + " : " + item.getEnchantments().toString());
			if(item.getEnchantments().containsKey(Enchantment.MENDING)){
				//経験値データを取得
				//便宜上毎回ここでNewしているが、メモリ的には優しくないはずなのでPlayerDataに含みたい...他でも使うし
				ExperienceManager expman = new ExperienceManager(player);
				int currentExp = expman.getCurrentExp();
				//経験値1に付き回復する耐久値（本家は経験値1=耐久値2）
				float mendingRate = 2.0f;
				//ツールの疲労度(回復する値)
				short curePoint = item.getDurability();
				//所持している経験値が全回復に足りるか
				if(curePoint > currentExp * mendingRate){
					curePoint = (short) (currentExp * mendingRate);
				}
				player.sendMessage("curePoint : " + curePoint);
				expman.changeExp(-curePoint / mendingRate);
				player.sendMessage("expman.changeExp");
				Repair(item, curePoint);
			}
			break;
		default:
			break;
		}

	}

	//指定した値分だけ耐久値を回復する
	static private void Repair(ItemStack item, short curePoint){
		item.setDurability((short) (item.getDurability() - curePoint));
//		//単純に値を変えるだけでは更新の関係でうまく動かないことがあるっぽい？
//		ItemStack newItem = item.clone();
//		//setDurability(0)が新品
//		short durability = (short) (item.getType().getMaxDurability() - curePoint);
//		newItem.setDurability(durability);
//		item = newItem;
	}
}
