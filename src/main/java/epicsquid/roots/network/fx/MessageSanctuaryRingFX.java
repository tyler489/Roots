package epicsquid.roots.network.fx;

import epicsquid.mysticallib.util.Util;
import epicsquid.roots.particle.ParticleUtil;
import epicsquid.roots.spell.SpellRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSanctuaryRingFX implements IMessage {
  private double posX = 0, posY = 0, posZ = 0;

  public MessageSanctuaryRingFX() {
    super();
  }

  public MessageSanctuaryRingFX(double x, double y, double z) {
    super();
    this.posX = x;
    this.posY = y;
    this.posZ = z;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    posX = buf.readDouble();
    posY = buf.readDouble();
    posZ = buf.readDouble();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeDouble(posX);
    buf.writeDouble(posY);
    buf.writeDouble(posZ);
  }

  public static float getColorCycle(float ticks) {
    return (MathHelper.sin((float) Math.toRadians(ticks)) + 1.0f) / 2.0f;
  }

  public static class MessageHolder implements IMessageHandler<MessageSanctuaryRingFX, IMessage> {
    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(final MessageSanctuaryRingFX message, final MessageContext ctx) {
      World world = Minecraft.getMinecraft().world;
      for (float k = 0; k < 360; k += Util.rand.nextInt(72)) {
        if (Util.rand.nextBoolean()) {
          if (Util.rand.nextBoolean()) {
            ParticleUtil.spawnParticlePetal(world, (float) message.posX + 3.0f * (float) Math.sin(Math.toRadians(k)), (float) message.posY,
                (float) message.posZ + 3.0f * (float) Math.cos(Math.toRadians(k)), 0, 0, 0, SpellRegistry.spell_sanctuary.getRed1(),
                SpellRegistry.spell_sanctuary.getGreen1(), SpellRegistry.spell_sanctuary.getBlue1(), 0.5f, 1.25f + 5.0f * Util.rand.nextFloat(), 40);
          } else {
            ParticleUtil.spawnParticlePetal(world, (float) message.posX + 3.0f * (float) Math.sin(Math.toRadians(k)), (float) message.posY,
                (float) message.posZ + 3.0f * (float) Math.cos(Math.toRadians(k)), 0, 0, 0, SpellRegistry.spell_sanctuary.getRed2(),
                SpellRegistry.spell_sanctuary.getGreen2(), SpellRegistry.spell_sanctuary.getBlue2(), 0.5f, 1.25f + 5.0f * Util.rand.nextFloat(), 40);
          }
        }
      }
      return null;
    }
  }

}