package net.spookysquad.spookster.render.clickgui;

import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.spookysquad.spookster.mod.HasValues;
import net.spookysquad.spookster.mod.HasValues.Value;
import net.spookysquad.spookster.mod.Module;
import net.spookysquad.spookster.render.FontUtil;
import net.spookysquad.spookster.render.GuiUtil;
import net.spookysquad.spookster.utils.Wrapper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ClickPanelItemFactory {

	public interface PanelItem {

		public void drawPanelItem(double posX, double posY, int x, int y);

		public boolean handleClick(double posX, double posY, int x, int y, boolean DistBelow2);
	}

	public static class NormalPanelItem extends GuiUtil implements PanelItem {

		public NormalPanelItem(Module module) {
			super();
			this.module = module;
		}

		public int xOffset = 0;
		public int yOffset = -4;

		public int xWidth = 105;
		public int yHeight = 12;

		protected long resetMS = -1L;
		static int timer = 0;
		static int xClickw, yClickw;
		private final Module module;

		@Override
		public void drawPanelItem(double posX, double posY, int x, int y) {
			drawNormalPanelItem(posX, posY);
			if (y >= (posY + yOffset) && y <= (posY + yHeight + yOffset)
					&& (x >= (posX + xOffset) && x <= (posX + xWidth + xOffset))) {
				drawPanelItemHover(posX, posY, x, y);
				if (timer < 5) {
					if (timer == 0) {
						resetMS = System.currentTimeMillis();
						xClickw = (int) posX;
						yClickw = (int) posY;
					}
					timer++;
				}
			} else if (x < xClickw + xOffset || x > xClickw + xWidth + xOffset || y > yClickw + yHeight + yOffset
					|| y < yClickw + yOffset) {
				timer = 0;
			}
			if (x < posX + xOffset || x > posX + xWidth + xOffset || y > posY + yHeight + yOffset || y < posY + yOffset) {
				resetPanelItemHover();
			}
		}

		public void resetPanelItemHover() {

		}

		public void drawNormalPanelItem(double posX, double posY) {
			drawRect(posX + xOffset, posY + yOffset, posX + xWidth + xOffset, posY + yHeight + yOffset, getModule().isEnabled() ? 0xFF2ECC71 : 0xFF2C3E50);
			drawStringWithShadow(getModule().getDisplay() + " [" + (getModule().getKeyCode() == -1 ? "-"
									: getModule().getKeyCode() > 256 ? Mouse.getButtonName(getModule().getKeyCode() - 256)
											: Keyboard.getKeyName(getModule().getKeyCode())) + "]", (float) posX + xOffset + 1, (float) posY - 1.5F, 0xFFFFFF);
		}

		public void drawPanelItemHover(double posX, double posY, int x, int y) {
			drawRect(posX + xOffset, posY + yOffset, posX + xWidth + xOffset, posY + yHeight + yOffset, getModule().isEnabled() ? 0xFF2ECC71 : 0xFF2C3E50);
			drawStringWithShadow(getModule().getDisplay() + " [" + (getModule().getKeyCode() == -1 ? "-"
					: getModule().getKeyCode() > 256 ? Mouse.getButtonName(getModule().getKeyCode() - 256)
							: Keyboard.getKeyName(getModule().getKeyCode())) + "]", (float) posX + xOffset + 1, (float) posY - 1.5F, 0xFFFFFF);
		}

		@Override
		public boolean handleClick(double posX, double posY, int x, int y, boolean distBelow2) {
			if (y >= (posY + yOffset - 1) && y <= (posY + yHeight + yOffset + 1)
					&& (x >= (posX + xOffset) && x <= (posX + xWidth + xOffset)) && distBelow2) {
				module.toggle(true);
				return true;
			}
			return false;
		}

		public boolean setListingKey(double posX, double posY, int x, int y) {
			if (y >= (posY + yOffset - 1) && y <= (posY + yHeight + yOffset + 1)
					&& (x >= (posX + xOffset) && x <= (posX + xWidth + xOffset))) {
				ClickScreen.getInstance().setModuleToChangeKey(this.module);
				return true;
			}
			return false;
		}

		public boolean handleDrag(double posX, double posY, int x, int y) {
			return false;
		}

		public static double getWidth(double width, double min, double max, double d) {
			if (d >= max) {
				d = max;
			} else if (d <= min) {
				d = min;
			}
			double w = width * (d - min);
			double f = w / (max - min);
			return f;
		}

		public Module getModule() {
			return module;
		}

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class EditablePanelItem extends NormalPanelItem {
		public boolean editProperties;
		protected ClickPanel panel;
		protected double fX, fY, ticky;
		private static final ResourceLocation ICON = new ResourceLocation("tabbychat", "textures/gui/icons/icon_editable.png");

		public EditablePanelItem(ClickPanel panel, Module module) {
			super(module);
			this.panel = panel;
			editProperties = false;
		}

		@Override
		public void drawNormalPanelItem(double posX, double posY) {
			yHeight = 12;
			drawRect(posX + xOffset, posY + yOffset, posX + xWidth + xOffset - 12, posY + yHeight + yOffset, getModule().isEnabled() ? 0xFF2ECC71 : 0xFF2C3E50);
			drawRect(posX + xOffset + xWidth - 15, posY + yOffset, posX + xWidth + xOffset, posY + yHeight + yOffset, 0xFF2C3E50);

			drawStringWithShadow(getModule().getDisplay() + " [" + (getModule().getKeyCode() == -1 ? "-"
					: getModule().getKeyCode() > 256 ? Mouse.getButtonName(getModule().getKeyCode() - 256)
							: Keyboard.getKeyName(getModule().getKeyCode())) + "]", (float) posX + xOffset + 1, (float) posY - 1.5F, 0xFFFFFF);
		
			if (this.editProperties) {
				int total = 2;
				HasValues hep = (HasValues) getModule();
				for (Value ps : hep.getValues()) {
					double width = getWidth(posX + xOffset + 1.5, posX + xWidth + xOffset - 1.5);
					Object ob = hep.getValue(ps.getName());
					if (ps.getMin() instanceof Integer) {
						Integer min = (Integer) ps.getMin();
						Integer max = (Integer) ps.getMax();
						int d = (Integer) ob;
						total += FontUtil.getFontHeight() * 0.5;
						drawStringWithShadow(ps.getName() + ": " + ob.toString(), (float) posX + 1, (float) posY + total, 0xFFFFFFFF, 0.7F);
						total += FontUtil.getFontHeight() * 0.5 + 1;
						drawRect(posX + 0.5, posY + total - 0.5, posX + width + 0.5, posY + 5 + total + 0.5, 0xFF2C3E50);
						drawRect(posX + 1.5, posY + total, posX + getWidth(width, min, max, d), posY + 5 + total, 0xFF2ECC71);
						drawRect(posX + getWidth(width, min, max, d) - (d <= min + 0.1 ? 0 : 0.5), posY + total, posX
								+ getWidth(width, min, max, d) + (d >= max - 0.1 ? 0 : 0.5), posY + 5 + total, 0xFF00FF7F);
					} else if (ps.getMin() instanceof Double) {
						Double min = (Double) ps.getMin();
						Double max = (Double) ps.getMax();
						double d = (Double) ob;
						total += FontUtil.getFontHeight() * 0.5;
						drawStringWithShadow(ps.getName() + ": " + ob.toString(), (float) posX + 1, (float) posY + total, 0xFFFFFFFF);
						total += FontUtil.getFontHeight() * 0.5 + 1;
						drawRect(posX + 0.5, posY + total - 0.5, posX + width + 0.5, posY + 5 + total + 0.5, 0xFF2C3E50);
						drawRect(posX + 1.5, posY + total, posX + getWidth(width, min, max, d), posY + 5 + total, 0xFF2ECC71);
						drawRect(posX + getWidth(width, min, max, d) - (d <= min + 0.1 ? 0 : 0.5), posY + total, posX
								+ getWidth(width, min, max, d) + (d >= max - 0.1 ? 0 : 0.5), posY + 5 + total, 0xFF00FF7F);
					} else if (ps.getMin() instanceof Float) {
						Float min = (Float) ps.getMin();
						Float max = (Float) ps.getMax();
						float d = (Float) ob;
						total += FontUtil.getFontHeight() * 0.5;
						drawStringWithShadow(ps.getName() + ": " + ob.toString(), (float) posX + 1, (float) posY + total, 0xFFFFFFFF);
						total += FontUtil.getFontHeight() * 0.5 + 1;
						drawRect(posX + 0.5, posY + total - 0.5, posX + width + 0.5, posY + 5 + total + 0.5, 0xFF2C3E50);
						drawRect(posX + 1.5, posY + total, posX + getWidth(width, min, max, d), posY + 5 + total, 0xFF2ECC71);
						drawRect(posX + getWidth(width, min, max, d) - (d <= min + 0.1 ? 0 : 0.5), posY + total, posX
								+ getWidth(width, min, max, d) + (d >= max - 0.1 ? 0 : 0.5), posY + 5 + total, 0xFF00FF7F);
					} else if (ps.getMin() instanceof Boolean) {
						boolean d = (Boolean) ob;
						int he = 10;
						int off = -4;
						total += 12;
						drawRect(posX + 1.5, posY + total + off, posX + width, posY + off + total + he, d ? 0xFF2ECC71 : 0xFF2C3E50);
						drawStringWithShadow(ps.getName(), (float) posX + 2, (float) posY + off + total + (he / 2 * 0.5F) - 1, 0xFFFFFFFF);
					}
				}
				yHeight += total;
			}
			drawEditableIcon(posX, posY);
		}

		@Override
		public void drawPanelItemHover(double posX, double posY, int x, int y) {
			drawNormalPanelItem(posX, posY);
		}

		public void drawEditableIcon(double posX, double posY) {
			float size = 8.999F;
			if (this.getModule().isEnabled()) drawTexturedRectangle(ICON, posX + xWidth - 10.5, posY - 3, size, size, 0.18F, 0.8F, 0.443F);
			else drawTexturedRectangle(ICON, posX + xWidth - 10.5, posY - 3, size, size, 1, 1, 1);
		}

		@Override
		public boolean handleClick(double posX, double posY, int x, int y, boolean distBelow2) {
			if (ticky == 1) {
				fX = 0;
				fY = 0;
				ticky = 0;
			}
			if (!distBelow2) return false;

			if (editProperties) {
				int total = 2;
				HasValues hep = (HasValues) getModule();
				for (Value ps : hep.getValues()) {
					double width = getWidth(posX + xOffset, posX + xWidth + xOffset - 1.5);
					Object ob = hep.getValue(ps.getName());
					if (ps.getMin() instanceof Integer) {
						int min = (Integer) ps.getMin();
						int max = (Integer) ps.getMax();
						int d = (int) (x - posX);
						int value = MathHelper.ceiling_double_int(getWidth(max - min, 0, width, d));
						total += FontUtil.getFontHeight() * 0.5;
						total += FontUtil.getFontHeight() * 0.5 + 1;
						if (x >= posX + 1.5 && x <= posX + width && y >= posY + total && y <= posY + 5 + total) {
							hep.setValue(ps.getName(), value + min);
							return true;
						}
					} else if (ps.getMin() instanceof Double) {
						double min = (Double) ps.getMin();
						double max = (Double) ps.getMax();
						double d = x - posX;
						double value = getWidth(max - min, 0, width, d);
						total += FontUtil.getFontHeight() * 0.5;
						total += FontUtil.getFontHeight() * 0.5 + 1;
						if (x >= posX + 1.5 && x <= posX + width && y >= posY + total && y <= posY + 5 + total) {
							hep.setValue(ps.getName(), value + min);
							return true;
						}
					} else if (ps.getMin() instanceof Float) {
						float min = (Float) ps.getMin();
						float max = (Float) ps.getMax();
						float d = (float) (x - posX);
						float value = (float) getWidth(max - min, 0, width, d);
						total += FontUtil.getFontHeight() * 0.5;
						total += FontUtil.getFontHeight() * 0.5 + 1;
						if (x >= posX + 1.5 && x <= posX + width && y >= posY + total && y <= posY + 5 + total) {
							hep.setValue(ps.getName(), value + min);
							return true;
						}
					} else if (ps.getMin() instanceof Boolean) {
						boolean d = (Boolean) ob;
						int he = 10;
						int off = -4;
						total += 12;
						if (x >= posX + 1.5 && y >= posY + total + off && x <= posX + width && y <= posY + off + total + he) {
							hep.setValue(ps.getName(), !d);
							return true;
						}
					}
				}
			}

			if (y >= (posY + yOffset) && y <= (posY + yHeight + yOffset)
					&& (x >= posX + xOffset + xWidth - 15 && x <= posX + xWidth + xOffset)) {
				editProperties = !editProperties;
				for (NormalPanelItem e : this.panel.getPanelItems()) {
					if (e instanceof EditablePanelItem) {
						EditablePanelItem ed = (EditablePanelItem) e;
						if (!e.getModule().getDisplay().equalsIgnoreCase(this.getModule().getDisplay()))
							ed.editProperties = false;
					}
				}
				return true;
			}

			if (y >= (posY + yOffset - 1) && y <= (posY + 10 + yOffset + 1)
					&& (x >= (posX + xOffset) && x <= (posX + xWidth + xOffset))) {
				getModule().toggle(true);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleDrag(double posX, double posY, int x, int y) {
			if (!this.editProperties || panel.isBeingDragged()) return false;

			if (ticky == 0) {
				fX = x;
				fY = y;
				ticky++;
			}

			if (editProperties) {
				int total = 2;
				HasValues hep = (HasValues) getModule();
				for (Value ps : hep.getValues()) {
					double width = getWidth(posX + xOffset, posX + xWidth + xOffset - 1.5);
					Object ob = hep.getValue(ps.getName());

					if (ps.getMin() instanceof Integer) {
						int min = (Integer) ps.getMin();
						int max = (Integer) ps.getMax();
						int d = (int) (x - posX);
						int value = MathHelper.ceiling_double_int(getWidth(max - min, 0, width, d));
						total += FontUtil.getFontHeight() * 0.5;
						total += FontUtil.getFontHeight() * 0.5 + 1;
						if (fX >= posX + 1.5 && fX <= posX + width && fY >= posY + total && fY <= posY + 5 + total) {
							hep.setValue(ps.getName(), value + min);
							return true;
						}
					} else if (ps.getMin() instanceof Double) {
						double min = (Double) ps.getMin();
						double max = (Double) ps.getMax();
						double d = x - posX;
						double value = getWidth(max - min, 0, width, d);
						total += FontUtil.getFontHeight() * 0.5;
						total += FontUtil.getFontHeight() * 0.5 + 1;

						if (fX >= posX + 1.5 && fX <= posX + width && fY >= posY + total && fY <= posY + 5 + total) {
							hep.setValue(ps.getName(), value + min);
							return true;
						}
					} else if (ps.getMin() instanceof Float) {
						float min = (Float) ps.getMin();
						float max = (Float) ps.getMax();
						float d = (float) (x - posX);
						float value = (float) getWidth(max - min, 0, width, d);
						total += FontUtil.getFontHeight() * 0.5;
						total += FontUtil.getFontHeight() * 0.5 + 1;
						if (fX >= posX + 1.5 && fX <= posX + width && fY >= posY + total && fY <= posY + 5 + total) {
							hep.setValue(ps.getName(), value + min);
							return true;
						}
					} else if (ps.getMin() instanceof Boolean) {
						total += 12;
					}
				}
			}
			return super.handleDrag(posX, posY, x, y);
		}

		public double getWidth(double posX, double posX2) {
			double width = posX2 - posX;
			return width;
		}
	}

	public static NormalPanelItem createPanelItem(ClickPanel panel, Module module) {
		NormalPanelItem item;
		if (module instanceof HasValues) {
			item = new EditablePanelItem(panel, module);
		} else {
			item = new NormalPanelItem(module);
		}
		return item;

	}

}