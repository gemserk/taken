package com.gemserk.games.taken;

import java.io.InputStream;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Element;

import com.gemserk.commons.svg.inkscape.SvgDocument;
import com.gemserk.commons.svg.inkscape.SvgDocumentHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroup;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroupHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapeImage;
import com.gemserk.commons.svg.inkscape.SvgInkscapeImageHandler;
import com.gemserk.commons.svg.inkscape.SvgParser;

public class WorldLoader {

	private SvgDocument svgDocument;

	private String layer;
	
	public WorldLoader(String layer) {
		this.layer = layer;
	}

	void loadWorld(InputStream scene) {

		SvgParser svgParser = new SvgParser();
		svgParser.addHandler(new SvgDocumentHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgDocument svgDocument, Element element) {
				WorldLoader.this.svgDocument = svgDocument;
			}
		});
		svgParser.addHandler(new SvgInkscapeGroupHandler() {

			@Override
			protected void handle(SvgParser svgParser, SvgInkscapeGroup svgInkscapeGroup, Element element) {

				if (isInkscapeLayer(svgInkscapeGroup) && !isLayer(svgInkscapeGroup)) {
					svgParser.processChildren(false);
					return;
				}

			}

			private boolean isLayer(SvgInkscapeGroup svgInkscapeGroup) {
				return svgInkscapeGroup.getLabel().equalsIgnoreCase(layer);
			}

			private boolean isInkscapeLayer(SvgInkscapeGroup svgInkscapeGroup) {
				return svgInkscapeGroup.getGroupMode().equals("layer");
			}

		});
		svgParser.addHandler(new SvgInkscapeImageHandler() {

			private boolean isFlipped(Matrix3f matrix) {
				return matrix.getM00() != matrix.getM11();
			}

			@Override
			protected void handle(SvgParser svgParser, SvgInkscapeImage svgImage, Element element) {

				if (svgImage.getLabel() == null)
					return;

				float width = svgImage.getWidth();
				float height = svgImage.getHeight();

				Matrix3f transform = svgImage.getTransform();

				Vector3f position = new Vector3f(svgImage.getX() + width * 0.5f, svgImage.getY() + height * 0.5f, 0f);
				transform.transform(position);

				Vector3f direction = new Vector3f(1f, 0f, 0f);
				transform.transform(direction);

				float angle = 360f - (float) (Math.atan2(direction.y, direction.x) * 180 / Math.PI);

				float sx = 1f;
				float sy = 1f;

				if (isFlipped(transform)) {
					sy = -1f;
				}

				float x = position.x;
				float y = svgDocument.getHeight() - position.y;

				if (element.hasAttribute("start"))
					handleCharacterStartPoint(x, y);
				else if (element.hasAttribute("robotStart"))
					handleRobotStartPoint(x, y);
				else
					handleStaticObject(svgImage, width, height, angle, sx, sy, x, y);

			}

		});
		svgParser.parse(scene);
	}

	protected void handleCharacterStartPoint(float x, float y) {
		
	}

	protected void handleRobotStartPoint(float x, float y) {
		
	}

	protected void handleStaticObject(SvgInkscapeImage svgImage, float width, float height, float angle, float sx, float sy, float x, float y) {
		
	}

}