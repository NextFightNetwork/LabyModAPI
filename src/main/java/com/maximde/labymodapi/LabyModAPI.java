package com.maximde.labymodapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

public class LabyModAPI extends JavaPlugin {
  private LabyModProtocol labyModProtocol;
  private static LabyModAPI instance;
  public void onEnable() {
    instance = this;
    labyModProtocol = new LabyModProtocol();
  }
  public static LabyModAPI getInstance() {
    return instance;
  }
  public LabyModProtocol getLabyModProtocol() {
    return labyModProtocol;
  }
  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
    if (!channel.equals("labymod3:main"))
      return;
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
    ByteBuf buf = Unpooled.wrappedBuffer(message);
    String key = this.labyModProtocol.readString(buf, 32767);
    String json = this.labyModProtocol.readString(buf, 32767);
    key.equals("INFO");
  }

  public void update(Player player, Integer Cash, Integer Bank) {
    updateBalanceDisplay(player, EnumBalanceType.CASH, true, Cash.intValue());
    updateBalanceDisplay(player, EnumBalanceType.BANK, true, Bank.intValue());
  }

  public void updateBalanceDisplay(Player player, EnumBalanceType type, boolean visible, int balance) {
    JsonObject economyObject = new JsonObject();
    JsonObject cashObject = new JsonObject();
    cashObject.addProperty("visible", Boolean.valueOf(visible));
    cashObject.addProperty("balance", Integer.valueOf(balance));
    economyObject.add(type.getKey(), (JsonElement)cashObject);
    this.labyModProtocol.sendLabyModMessage(player, "economy", (JsonElement)economyObject);
  }

  public void updateBalanceDisplay(Player player, EnumBalanceType type, boolean visible, int balance, boolean decimal) {
    JsonObject economyObject = new JsonObject();
    JsonObject cashObject = new JsonObject();
    cashObject.addProperty("visible", Boolean.valueOf(visible));
    cashObject.addProperty("balance", Integer.valueOf(balance));
    if (decimal) {
      JsonObject decimalObject = new JsonObject();
      decimalObject.addProperty("format", "##.##");
      decimalObject.addProperty("divisor", Integer.valueOf(100));
      cashObject.add("decimal", (JsonElement)decimalObject);
    }
    economyObject.add(type.getKey(), (JsonElement)cashObject);
    this.labyModProtocol.sendLabyModMessage(player, "economy", (JsonElement)economyObject);
  }

  public void updateBalanceDisplay(Player player, EnumBalanceType type, boolean visible, int balance, String iconlink, boolean decimal) {
    JsonObject economyObject = new JsonObject();
    JsonObject cashObject = new JsonObject();
    cashObject.addProperty("visible", Boolean.valueOf(visible));
    cashObject.addProperty("balance", Integer.valueOf(balance));
    cashObject.addProperty("icon", iconlink);
    if (decimal) {
      JsonObject decimalObject = new JsonObject();
      decimalObject.addProperty("format", "##.##");
      decimalObject.addProperty("divisor", Integer.valueOf(100));
      cashObject.add("decimal", (JsonElement)decimalObject);
    }
    economyObject.add(type.getKey(), (JsonElement)cashObject);
    this.labyModProtocol.sendLabyModMessage(player, "economy", (JsonElement)economyObject);
  }

  public enum EnumBalanceType {
    CASH("cash"),
    BANK("bank");

    private final String key;

    EnumBalanceType(String key) {
      this.key = key;
    }

    public String getKey() {
      return this.key;
    }
  }

  public void setSubtitleVersion(Player receiver, UUID subtitlePlayer, String value, Double range) {
    JsonArray array = new JsonArray();
    JsonObject subtitle = new JsonObject();
    subtitle.addProperty("uuid", subtitlePlayer.toString());
    subtitle.addProperty("size", Double.valueOf(0.8D));
    if (value != null)
      subtitle.addProperty("value", value);
    array.add((JsonElement)subtitle);
    this.labyModProtocol.sendLabyModMessage(receiver, "account_subtitle", (JsonElement)array);
  }

  public void sendServerBanner(Player player, String imageUrl) {
    JsonObject object = new JsonObject();
    object.addProperty("url", imageUrl);
    this.labyModProtocol.sendLabyModMessage(player, "server_banner", (JsonElement)object);
  }

  public void sendFlag(Player receiver, UUID uuid, String countryCode) {
    JsonObject flagPacket = new JsonObject();
    JsonArray users = new JsonArray();
    JsonObject userObject = new JsonObject();
    userObject.addProperty("uuid", uuid.toString());
    userObject.addProperty("code", countryCode);
    users.add((JsonElement)userObject);
    flagPacket.add("users", (JsonElement)users);
    this.labyModProtocol.sendLabyModMessage(receiver, "language_flag", (JsonElement)flagPacket);
  }

  public void sendWatermark(Player player, boolean visible) {
    JsonObject object = new JsonObject();
    object.addProperty("visible", Boolean.valueOf(visible));
    this.labyModProtocol.sendLabyModMessage(player, "watermark", (JsonElement)object);
  }

  public void updateGameInfo(Player player, boolean hasGame, String gamemode, long startTime, long endTime) {
    JsonObject obj = new JsonObject();
    obj.addProperty("hasGame", Boolean.valueOf(hasGame));
    if (hasGame) {
      obj.addProperty("game_mode", gamemode);
      obj.addProperty("game_startTime", Long.valueOf(startTime));
      obj.addProperty("game_endTime", Long.valueOf(endTime));
    }
    this.labyModProtocol.sendLabyModMessage(player, "discord_rpc", (JsonElement)obj);
  }

  public void updatePartyInfo(Player player, String domain, boolean hasParty, UUID partyLeaderUUID, int partySize, int maxPartyMembers) {
    JsonObject obj = new JsonObject();
    obj.addProperty("hasParty", Boolean.valueOf(hasParty));
    if (hasParty) {
      obj.addProperty("partyId", String.valueOf(partyLeaderUUID.toString()) + ":" + domain);
      obj.addProperty("party_size", Integer.valueOf(partySize));
      obj.addProperty("party_max", Integer.valueOf(maxPartyMembers));
    }
    this.labyModProtocol.sendLabyModMessage(player, "discord_rpc", (JsonElement)obj);
  }

  public void forceEmote(Player receiver, UUID npcUUID, int emoteId) {
    JsonArray array = new JsonArray();
    JsonObject forcedEmote = new JsonObject();
    forcedEmote.addProperty("uuid", npcUUID.toString());
    forcedEmote.addProperty("emote_id", Integer.valueOf(emoteId));
    array.add((JsonElement)forcedEmote);
    this.labyModProtocol.sendLabyModMessage(receiver, "emote_api", (JsonElement)array);
  }

  public void sendCurrentPlayingGamemode(Player player, boolean visible, String gamemodeName) {
    JsonObject object = new JsonObject();
    object.addProperty("show_gamemode", Boolean.valueOf(visible));
    object.addProperty("gamemode_name", gamemodeName);
    this.labyModProtocol.sendLabyModMessage(player, "server_gamemode", (JsonElement)object);
  }

  public void forceSticker(Player receiver, UUID npcUUID, short stickerId) {
    JsonArray array = new JsonArray();
    JsonObject forcedSticker = new JsonObject();
    forcedSticker.addProperty("uuid", npcUUID.toString());
    forcedSticker.addProperty("sticker_id", Short.valueOf(stickerId));
    array.add((JsonElement)forcedSticker);
    this.labyModProtocol.sendLabyModMessage(receiver, "sticker_api", (JsonElement)array);
  }

  public void sendClientToServer(Player player, String title, String address, boolean preview) {
    JsonObject object = new JsonObject();
    object.addProperty("title", title);
    object.addProperty("address", address);
    object.addProperty("preview", Boolean.valueOf(preview));
    this.labyModProtocol.sendLabyModMessage(player, "server_switch", (JsonElement)object);
  }

  public void sendCineScope(Player player, int coveragePercent, long duration) {
    JsonObject object = new JsonObject();
    object.addProperty("coverage", Integer.valueOf(coveragePercent));
    object.addProperty("duration", Long.valueOf(duration));
    this.labyModProtocol.sendLabyModMessage(player, "cinescopes", (JsonElement)object);
  }
}
