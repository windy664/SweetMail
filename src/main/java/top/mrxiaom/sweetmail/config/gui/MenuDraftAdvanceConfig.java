package top.mrxiaom.sweetmail.config.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweetmail.SweetMail;
import top.mrxiaom.sweetmail.config.AbstractMenuConfig;
import top.mrxiaom.sweetmail.func.DraftManager;
import top.mrxiaom.sweetmail.func.data.Draft;
import top.mrxiaom.sweetmail.gui.AbstractDraftGui;
import top.mrxiaom.sweetmail.utils.ChatPrompter;
import top.mrxiaom.sweetmail.utils.Pair;
import top.mrxiaom.sweetmail.utils.Util;
import top.mrxiaom.sweetmail.utils.comp.PAPI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import static top.mrxiaom.sweetmail.utils.Pair.replace;

public class MenuDraftAdvanceConfig extends AbstractMenuConfig<MenuDraftAdvanceConfig.Gui> {
    Icon iconSenderDisplay;
    String iconSenderDisplayPromptTips;
    String iconSenderDisplayPromptCancel;
    String iconSenderDisplayUnset;
    Icon iconReceivers;
    String iconReceiversPrompts3Tips;
    String iconReceiversPrompts3Cancel;
    String iconReceiversPrompts4TipsStart;
    String iconReceiversPrompts4TipsEnd;
    String iconReceiversPrompts4Cancel;
    String iconReceiversUnset;
    String iconReceiversBadTimeFormat;
    Icon iconTimed;
    String iconTimedPromptTips;
    String iconTimedPromptCancel;
    Icon iconBack;
    public MenuDraftAdvanceConfig(SweetMail plugin) {
        super(plugin, "menus/draft_advance.yml");
    }

    @Override
    public void reloadConfig(MemoryConfiguration cfg) {
        super.reloadConfig(cfg);
    }

    @Override
    protected void clearMainIcons() {
        iconBack = null;
    }

    @Override
    protected void loadMainIcon(ConfigurationSection section, String key, Icon loadedIcon) {
        switch (key) {
            case "系": {
                iconSenderDisplay = loadedIcon;
                iconSenderDisplayPromptTips = section.getString(key + ".prompt-tips", "&7[&e&l邮件&7] &b请在聊天栏发送&e“发件人显示名称”&b的值 &7(输入 &ccancel &7取消设置)");
                iconSenderDisplayPromptCancel = section.getString(key + ".prompt-cancel", "cancel");
                iconSenderDisplayUnset = section.getString(key + ".unset", "&7未设置");
            }
            case "收": {
                iconReceivers = loadedIcon;
                iconReceiversPrompts3Tips = section.getString(key + ".prompt-tips", "&7[&e&l邮件&7] &b请在聊天栏发送，&f“多久之前到现在，上过线的玩家”&b的 &e判定起始时间 &7(格式 &f年-月-日 时:分:秒&7，不输入时分秒部分默认为0。输入 &ccancel &7取消设置)");
                iconReceiversPrompts3Cancel = section.getString(key + ".prompt-cancel", "cancel");
                iconReceiversPrompts4TipsStart = section.getString(key + ".prompt-tips", "&7[&e&l邮件&7] &b请在聊天栏发送，&f“在某段时间内上过线的玩家”&b的 &e判定起始时间 &7(格式 &f年-月-日 时:分:秒&7，不输入时分秒部分默认为0。输入 &ccancel &7取消设置)");
                iconReceiversPrompts4TipsEnd = section.getString(key + ".prompt-tips", "&7[&e&l邮件&7] &b请在聊天栏发送，&f“在某段时间内上过线的玩家”&b的 &e判定结束时间 &7(格式 &f年-月-日 时:分:秒&7，不输入时分秒部分默认为0。输入 &ccancel &7取消设置)");
                iconReceiversPrompts4Cancel = section.getString(key + ".prompt-cancel", "cancel");
                iconReceiversUnset = section.getString(key + ".unset", "&7未设置");
                iconReceiversBadTimeFormat = section.getString(key + ".bad-time-format", "&7[&e&l邮件&7] &f你输入的时间格式不正确!");
            }
            case "定": {
                iconTimed = loadedIcon;
                iconTimedPromptTips = section.getString(key + ".prompt-tips", "&7[&e&l邮件&7] &b请在聊天栏发送&e“定时发送时间”&b的值，并立即加入定时发送队列 &7(格式 &f年-月-日 时:分:秒&7，不输入时分秒部分默认为0。输入 &ccancel &7取消定时发送)");
                iconTimedPromptCancel = section.getString(key + ".prompt-cancel", "cancel");
            }
            case "返": {
                iconBack = loadedIcon;
                break;
            }
        }
    }

    @Override
    public Inventory createInventory(Gui gui, Player target) {
        return Bukkit.createInventory(null, inventory.length, replace(PAPI.setPlaceholders(target, title)));
    }

    @Override
    protected ItemStack tryApplyMainIcon(Gui gui, String key, Player target, int iconIndex) {
        DraftManager manager = DraftManager.inst();
        Draft draft = manager.getDraft(target);
        switch (key) {
            case "系": {
                String senderDisplay = draft.advSenderDisplay == null || draft.advSenderDisplay.isEmpty()
                        ? iconSenderDisplayUnset
                        : draft.advSenderDisplay;
                return iconSenderDisplay.generateIcon(target, Pair.of("%sender%", senderDisplay));
            }
            case "收": {
                String receivers = draft.advReceivers == null || draft.advReceivers.isEmpty()
                        ? iconReceiversUnset
                        : draft.advReceivers;
                return iconReceivers.generateIcon(target, Pair.of("%receivers%", receivers));
            }
            case "定": {
                return iconTimed.generateIcon(target);
            }
            case "返": {
                return iconBack.generateIcon(target);
            }
        }
        return null;
    }

    public static MenuDraftAdvanceConfig inst() {
        return get(MenuDraftAdvanceConfig.class).orElseThrow(IllegalStateException::new);
    }

    public class Gui extends AbstractDraftGui {
        public Gui(SweetMail plugin, Player player) {
            super(plugin, player);
        }

        @Override
        public Inventory newInventory() {
            Inventory inv = createInventory(this, player);
            applyIcons(this, inv, player);
            return inv;
        }

        @Override
        public void onClick(InventoryAction action, ClickType click, InventoryType.SlotType slotType, int slot, ItemStack currentItem, ItemStack cursor, InventoryView view, InventoryClickEvent event) {
            Character c = getSlotKey(slot);
            if (c == null) return;
            event.setCancelled(true);

            switch (String.valueOf(c)) {
                case "系": {
                    if (!click.isShiftClick()) {
                        if (click.isLeftClick()) {
                            player.closeInventory();
                            ChatPrompter.prompt(
                                    plugin, player,
                                    iconSenderDisplayPromptTips,
                                    iconSenderDisplayPromptCancel,
                                    advSenderDisplay -> {
                                        draft.advSenderDisplay = advSenderDisplay;
                                        draft.save();
                                        reopen.run();
                                    }, reopen);
                        }
                        if (click.isRightClick()) {
                            draft.advSenderDisplay = null;
                            draft.save();
                            applyIcon(this, view, player, slot);
                            Util.updateInventory(player);
                        }
                    }
                    return;
                }
                case "收": {
                    if (!click.isShiftClick()) {
                        if (click.isRightClick()) {
                            draft.advReceivers = null;
                            draft.save();
                            applyIcon(this, view, player, slot);
                            Util.updateInventory(player);
                        }
                        if (click.equals(ClickType.NUMBER_KEY)) {
                            int btn = event.getHotbarButton() + 1;
                            switch (btn) {
                                case 1: {
                                    draft.advReceivers = "current online";
                                    draft.save();
                                    break;
                                }
                                case 2: {
                                    draft.advReceivers = "current online bungeecord";
                                    draft.save();
                                    break;
                                }
                                case 3: {
                                    player.closeInventory();
                                    ChatPrompter.prompt(
                                            plugin, player,
                                            iconSenderDisplayPromptTips,
                                            iconSenderDisplayPromptCancel,
                                            timeStr -> {
                                                Long timestamp = parseTime(timeStr);
                                                if (timestamp == null) {
                                                    t(player, iconReceiversBadTimeFormat);
                                                } else {
                                                    draft.advReceivers = "last played in " + timestamp;
                                                    draft.save();
                                                }
                                                reopen.run();
                                            }, reopen);
                                    return;
                                }
                                case 4: {
                                    player.closeInventory();
                                    ChatPrompter.prompt(
                                            plugin, player,
                                            iconSenderDisplayPromptTips,
                                            iconSenderDisplayPromptCancel,
                                            timeStr1 -> {
                                                Long timestampStart = parseTime(timeStr1);
                                                if (timestampStart == null) {
                                                    t(player, iconReceiversBadTimeFormat);
                                                    reopen.run();
                                                } else ChatPrompter.prompt(
                                                        plugin, player,
                                                        iconSenderDisplayPromptTips,
                                                        iconSenderDisplayPromptCancel,
                                                        timeStr2 -> {
                                                            Long timestampEnd = parseTime(timeStr2);
                                                            if (timestampEnd == null) {
                                                                t(player, iconReceiversBadTimeFormat);
                                                            } else {
                                                                draft.advReceivers = "last played from " + timestampStart + " to " + timestampEnd;
                                                                draft.save();
                                                            }
                                                            reopen.run();
                                                        }, reopen);
                                            }, reopen);
                                    break;
                                }
                                default:
                                    return;
                            }
                            applyIcon(this, view, player, slot);
                            Util.updateInventory(player);
                        }
                    }
                    return;
                }
                case "返": {
                    if (click.isLeftClick() && !click.isShiftClick()) {
                        MenuDraftConfig.inst()
                                .new Gui(plugin, player)
                                .open();
                    }
                    return;
                }
                default: {
                    handleClick(player, click, c);
                }
            }
        }
    }

    @Nullable
    public static Long parseTime(String s) {
        String[] split = s.split(" ", 2);
        try {
            LocalDate localDate = LocalDate.parse(split[0]);
            LocalTime localTime = split.length > 1
                ? LocalTime.parse(split[1])
                : LocalTime.of(0, 0, 0);
            LocalDateTime time = localDate.atTime(localTime);
            return time.toEpochSecond(ZoneOffset.UTC) * 1000L;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
