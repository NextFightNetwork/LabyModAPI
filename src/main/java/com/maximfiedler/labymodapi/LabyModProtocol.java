package com.maximfiedler.labymodapi;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.charset.Charset;

public class LabyModProtocol {
  public void sendLabyModMessage(Player player, String key, JsonElement messageContent) {
    byte[] bytes = getBytesToSend(key, messageContent.toString());
    PacketDataSerializer pds = new PacketDataSerializer(Unpooled.wrappedBuffer(bytes));
    PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload(new MinecraftKey("labymod3:main"), pds);
    (((CraftPlayer)player).getHandle()).c.a(payloadPacket);
  }

  public byte[] getBytesToSend(String messageKey, String messageContents) {
    ByteBuf byteBuf = Unpooled.buffer();
    writeString(byteBuf, messageKey);
    writeString(byteBuf, messageContents);
    byte[] bytes = new byte[byteBuf.readableBytes()];
    byteBuf.readBytes(bytes);
    byteBuf.release();
    return bytes;
  }

  public void writeVarIntToBuffer(ByteBuf buf, int input) {
    while ((input & 0xFFFFFF80) != 0) {
      buf.writeByte(input & 0x7F | 0x80);
      input >>>= 7;
    }
    buf.writeByte(input);
  }

  public void writeString(ByteBuf buf, String string) {
    byte[] abyte = string.getBytes(Charset.forName("UTF-8"));
    if (abyte.length > 32767)
      throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + ")");
    writeVarIntToBuffer(buf, abyte.length);
    buf.writeBytes(abyte);
  }

  public int readVarIntFromBuffer(ByteBuf buf) {
    byte b0;
    int i = 0;
    int j = 0;
    do {
      b0 = buf.readByte();
      i |= (b0 & Byte.MAX_VALUE) << j++ * 7;
      if (j > 5)
        throw new RuntimeException("VarInt too big");
    } while ((b0 & 0x80) == 128);
    return i;
  }

  public String readString(ByteBuf buf, int maxLength) {
    int i = readVarIntFromBuffer(buf);
    if (i > maxLength * 4)
      throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + (maxLength * 4) + ")");
    if (i < 0)
      throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
    byte[] bytes = new byte[i];
    buf.readBytes(bytes);
    String s = new String(bytes, Charset.forName("UTF-8"));
    if (s.length() > maxLength)
      throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
    return s;
  }
}
