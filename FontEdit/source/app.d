import arsd.simpledisplay;
import arsd.nanovega;

import gl3n.linalg;

import std.algorithm;
import std.array;
import std.conv : to;
import std.math;
import std.range;
import std.string;

int grid = 8;

enum float viewportWidth = 400;
enum float viewportHeight = 400;

SimpleWindow sdmain;
NVGContext nvg;

float canvasX() @property
{
	return (sdmain.width - viewportWidth) / 2;
}

float canvasY() @property
{
	return (sdmain.height - viewportHeight) / 2;
}

float globalToCanvasX(float x)
{
	return (x - canvasX) / viewportWidth;
}

float globalToCanvasY(float y)
{
	return (y - canvasY) / viewportHeight;
}

struct Line
{
	enum Type
	{
		linear,
		quadraticBezier,
		cubicBezier
	}

	Type type;
	vec2[] points;
	int activePoint = -1;

	bool fitsMorePoints()
	{
		final switch (type)
		{
		case Type.linear:
			return true;
		case Type.quadraticBezier:
			return points.length < 4;
		case Type.cubicBezier:
			return points.length < 3;
		}
	}

	string toListViewText() const @property
	{
		final switch (type)
		{
		case Type.linear:
			return "Line";
		case Type.quadraticBezier:
			return "Quadratic Bézier";
		case Type.cubicBezier:
			return "Cubic Bézier";
		}
	}

	void drawListView(ScrollItem!Line item, ScrollView!Line view)
	{
		nvg.text(item.x + view.padding.left, item.y + view.padding.top + nvg.textFontAscender,
				item.text);
		nvg.beginPath();
		nvg.strokeColor = NVGColor.red;
		nvg.strokeWidth = 4;
		float d = item.height - view.padding.top - view.padding.bottom;
		nvg.moveTo(item.x + item.width - view.padding.right - d, view.padding.top + item.y);
		nvg.lineTo(item.x + item.width - view.padding.right, view.padding.top + d + item.y);

		nvg.moveTo(item.x + item.width - view.padding.right - d, view.padding.top + d + item.y);
		nvg.lineTo(item.x + item.width - view.padding.right, view.padding.top + item.y);
		nvg.stroke();
	}

	void click(ScrollItem!Line item, ScrollView!Line view, int index, float x, float y)
	{
		import std.algorithm : remove;

		float d = item.height - view.padding.top - view.padding.bottom;
		if (x > item.width - view.padding.right * 2 - d)
		{
			// remove
			auto target = this;
			lines = lines.remove(index);
		}
		else
		{
			// select
			editIndex = index;
		}
	}
}

string serializeLines()
{
	vec2 lastPos = vec2(float.max, -float.max);
	auto ret = appender!string;
	foreach (line; lines)
	{
		if (line.points.length == 0)
			continue;
		vec2 p = line.points[0] * 8;
		if ((lastPos - p).length_squared > 0.00001f)
			ret ~= "M" ~ str(p);

		final switch (line.type)
		{
		case Line.Type.linear:
			foreach (point; line.points[1 .. $])
			{
				p = point * 8;
				if (abs(p.x - lastPos.x) < 0.001f)
					ret ~= "V" ~ str(p.y);
				else if (abs(p.y - lastPos.y) < 0.001f)
					ret ~= "H" ~ str(p.x);
				else
					ret ~= "L" ~ str(p);
				lastPos = p;
			}
			break;
		case Line.Type.cubicBezier:
			if (line.points.length >= 4)
			{
				ret ~= "C" ~ str(line.points[1] * 8) ~ " " ~ str(
						line.points[2] * 8) ~ " " ~ str(line.points[3] * 8);
				lastPos = line.points[3] * 8;
			}
			break;
		case Line.Type.quadraticBezier:
			if (line.points.length >= 3)
			{
				ret ~= "Q" ~ str(line.points[1] * 8) ~ " " ~ str(line.points[2] * 8);
				lastPos = line.points[2] * 8;
			}
			break;
		}
	}
	return ret.data;
}

private string str(float n)
{
	auto ret = n.to!string.replace(",", ".");
	auto dot = ret.indexOf('.');
	if (dot == -1)
		return ret;
	else if (dot + 5 < ret.length)
		return ret[0 .. dot + 5];
	else
		return ret;
}

private string str(vec2 v)
{
	return v.x.str ~ "," ~ v.y.str;
}

struct ScrollItem(T)
{
	T data;
	string text;
	float width = 100;
	float height = 10;
	float x = 0, y = 0;
}

struct Padding
{
	float left = 0, top = 0, right = 0, bottom = 0;

	this(float all)
	{
		this(all, all);
	}

	this(float horizontal, float vertical)
	{
		left = right = horizontal;
		top = bottom = vertical;
	}

	this(float left, float top, float right, float bottom)
	{
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
}

struct ScrollView(T)
{
	T[] items;
	float width = 200, height = 100;
	float scroll = 0;
	Padding padding;
	int hoverIndex = -1;
	bool mouseDown, mouseMoved;
	int clickX, clickY;
	float lineHeight;

	auto layout()
	{
		static struct ScrollViewLayout
		{
			ScrollView!T view;
			T[] items;
			float y = 0;
			ScrollItem!T current;

			void popFront()
			{
				y += front.height;
				items.popFront;
				current = ScrollItem!T.init;
			}

			bool empty() @property
			{
				return items.empty;
			}

			ScrollItem!T front() @property
			{
				if (current == ScrollItem!T.init)
				{
					current.width = view.width;
					current.data = items.front;
					static if (is(typeof(current.data.toListViewText)))
						current.text = current.data.toListViewText;
					else
						current.text = current.data.to!string;
					current.height = view.lineHeight + view.padding.top + view.padding.bottom;
					current.x = 0;
					current.y = y;
					return current;
				}
				else
					return current;
			}
		}

		return ScrollViewLayout(this, items);
	}

	void draw()
	{
		lineHeight = nvg.fontSize * nvg.textLineHeight;
		nvg.scissor(0, 0, width, height);
		nvg.translate(0, -scroll);
		foreach (item; layout)
		{
			nvg.beginPath();
			nvg.roundedRect(item.x, item.y, item.width, item.height, 6);
			nvg.fillPaint = nvg.linearGradient(item.x, item.y, item.x,
					item.y + item.height, NVGColor(1, 1, 1, 0.35), NVGColor(1, 1, 1, 0.5));
			nvg.fill();
			nvg.strokeColor = NVGColor.white;
			nvg.strokeWidth = 2;
			nvg.stroke();
			nvg.fillColor = NVGColor.black;
			static if (is(typeof(item.data.drawListView)))
				item.data.drawListView(item, this);
			else
				nvg.text(item.x + padding.left, item.y + padding.top + nvg.textFontAscender, item.text);
		}
		nvg.translate(0, scroll);
		nvg.resetScissor();
	}

	bool processMouse(MouseEvent event)
	{
		int oldHoverIndex = hoverIndex;
		if ((event.x < 0 || event.y < 0 || event.x > width || event.y > height) && !mouseDown)
		{
			hoverIndex = -1;
		}
		else if (event.type == MouseEventType.buttonPressed)
		{
			mouseDown = true;
			mouseMoved = false;
		}
		else if (event.type == MouseEventType.buttonReleased)
		{
			mouseDown = false;
			if (!mouseMoved)
			{
				if (hoverIndex >= 0 && hoverIndex < items.length)
				{
					auto last = layout.drop(hoverIndex).front;
					static if (is(typeof(last.data.click)))
						last.data.click(last, this, hoverIndex, event.x - last.x, event.y - last.y);
					return true;
				}
			}
		}
		else if (event.type == MouseEventType.motion)
		{
			mouseMoved = true;
			if (mouseDown)
			{
				if (items.length > 0)
				{
					int prevScroll = cast(int) scroll;
					scroll -= event.dy;
					auto l = layout;
					auto last = l.front;
					while (!l.empty)
					{
						last = l.front;
						l.popFront;
					}
					float max = max(0, last.y + last.height - height);
					if (scroll < 0)
						scroll = 0;
					else if (scroll > max)
						scroll = max;
					return prevScroll != cast(int) scroll;
				}
				else
				{
					return false;
				}
			}
			else
			{
				int i = -1;
				foreach (item; layout)
				{
					i++;
					float y = item.y - scroll;
					if (event.y >= y && event.y <= y + item.height)
					{
						hoverIndex = i;
						break;
					}
				}
			}
		}
		return oldHoverIndex != hoverIndex;
	}
}

Line[] lines = [Line(Line.Type.cubicBezier, [vec2(0, 0), vec2(1, 0), vec2(1, 1), vec2(0, 1)])];
ScrollView!Line linesView;
int editIndex = 0;
int font;

enum float grabRadius = 8 / viewportWidth;
enum float mouseGrabRadius = grabRadius * 3;

bool hold(float x, float y, bool ctrl)
{
	if (editIndex >= 0 && editIndex < lines.length)
	{
		Line line = lines[editIndex];

		if (!ctrl && line.points.length > 0)
		{
			auto oldActive = lines[editIndex].activePoint;
			if (oldActive >= 0 && oldActive < line.points.length
					&& (line.points[oldActive] - vec2(x, y)).length_squared
					<= mouseGrabRadius * mouseGrabRadius)
				return false;
			lines[editIndex].activePoint = -1;
			foreach (i, point; line.points)
			{
				if ((point - vec2(x, y)).length_squared <= mouseGrabRadius * mouseGrabRadius)
				{
					lines[editIndex].activePoint = cast(int) i;
				}
			}
			return lines[editIndex].activePoint != oldActive;
		}
	}
	return false;
}

bool click(float x, float y, bool ctrl)
{
	if (editIndex >= 0 && editIndex < lines.length)
	{
		Line line = lines[editIndex];

		if (ctrl || line.points.length == 0)
		{
			if (line.fitsMorePoints)
			{
				lines[editIndex].points ~= vec2(x, y);
				return true;
			}
			else
				return false;
		}
		else
		{
			auto oldActive = lines[editIndex].activePoint;
			lines[editIndex].activePoint = -1;
			foreach (i, point; line.points)
			{
				if ((point - vec2(x, y)).length_squared <= mouseGrabRadius * mouseGrabRadius)
				{
					if (cast(int) i == oldActive)
						break;
					lines[editIndex].activePoint = cast(int) i;
				}
			}
			return lines[editIndex].activePoint != oldActive;
		}
	}
	return false;
}

bool drag(float x, float y, float dx, float dy)
{
	if (editIndex >= 0 && editIndex < lines.length)
	{
		Line line = lines[editIndex];
		if (line.activePoint >= 0 && line.activePoint < line.points.length)
		{
			vec2 c = clampToGrid(vec2(x, y), line.activePoint == 0
					|| line.activePoint == line.points.length - 1);
			if ((c - vec2(line.points[line.activePoint].x,
					line.points[line.activePoint].y)).length_squared < 0.00000001)
				return false;
			line.points[line.activePoint].x = c.x;
			line.points[line.activePoint].y = c.y;
			return true;
		}
	}
	return false;
}

vec2 clampToGrid(vec2 position, bool clampInside)
{
	auto ret = vec2(round(position.x * grid) / grid, round(position.y * grid) / grid);
	if (clampInside)
	{
		if (ret.x < 0)
			ret.x = 0;
		if (ret.x > 1)
			ret.x = 1;
		if (ret.y < 0)
			ret.y = 0;
		if (ret.y > 1)
			ret.y = 1;
	}
	return ret;
}

void drawCanvas()
{
	foreach (i, line; lines)
	{
		if (line.points.length == 0)
			continue;

		nvg.beginPath();
		nvg.moveTo(line.points[0].x, line.points[0].y);
		final switch (line.type)
		{
		case Line.Type.linear:
			foreach (cp; line.points[1 .. $])
				nvg.lineTo(cp.x, cp.y);
			break;
		case Line.Type.cubicBezier:
			if (line.points.length >= 4)
				nvg.bezierTo(line.points[1].x, line.points[1].y, line.points[2].x,
						line.points[2].y, line.points[3].x, line.points[3].y);
			break;
		case Line.Type.quadraticBezier:
			if (line.points.length >= 3)
				nvg.quadTo(line.points[1].x, line.points[1].y, line.points[2].x, line.points[2].y);
			break;
		}
		nvg.strokeColor = NVGColor.white;
		nvg.strokeWidth = 2 / viewportWidth;
		nvg.stroke();

		if (editIndex == i)
		{

			nvg.beginPath();
			nvg.circle(line.points[0].x, line.points[0].y, grabRadius);
			nvg.strokeColor = NVGColor.red;
			nvg.strokeWidth = 2 / viewportWidth;
			nvg.stroke();

			if (line.type == Line.Type.cubicBezier && line.points.length >= 4)
			{
				nvg.beginPath();
				nvg.circle(line.points[0].x, line.points[0].y, grabRadius);
				nvg.strokeColor = NVGColor(1, 1, 1, 0.5);
				nvg.strokeWidth = 2 / viewportWidth;
				nvg.stroke();
			}

			if (line.points.length >= 2)
			{
				foreach (p; line.points[1 .. $ - 1])
				{
					nvg.beginPath();
					nvg.circle(p.x, p.y, grabRadius);
					nvg.strokeColor = NVGColor.lime;
					nvg.strokeWidth = 2 / viewportWidth;
					nvg.stroke();
				}

				nvg.beginPath();
				nvg.circle(line.points[$ - 1].x, line.points[$ - 1].y, grabRadius);
				nvg.strokeColor = NVGColor.blue;
				nvg.strokeWidth = 2 / viewportWidth;
				nvg.stroke();
			}

			if (line.activePoint >= 0 && line.activePoint < line.points.length)
			{
				nvg.beginPath();
				nvg.circle(line.points[line.activePoint].x, line.points[line.activePoint].y, grabRadius);
				nvg.strokeColor = NVGColor.yellow;
				nvg.strokeWidth = 6 / viewportWidth;
				nvg.stroke();
				nvg.strokeColor = line.activePoint == 0 ? NVGColor.red
					: line.activePoint == line.points.length - 1 ? NVGColor.blue : NVGColor.lime;
				nvg.strokeWidth = 2 / viewportWidth;
				nvg.stroke();
			}
		}
	}
}

void main()
{
	setOpenGLContextVersion(3, 0);

	sdmain = new SimpleWindow(800, 600, "NanoVega Simple Sample",
			OpenGlOptions.yes, Resizability.allowResizing);

	sdmain.onClosing = delegate() { nvg.kill(); };
	sdmain.visibleForTheFirstTime = delegate() {
		nvg = nvgCreateContext();

		if (nvg is null)
			assert(0, "cannot initialize NanoVega");

		font = nvg.createFont("Exo 2", "Exo2-Regular.ttf");
		if (font == -1)
			assert(0, "cannot load Exo 2 font");
		nvg.fontFaceId = font;
	};

	sdmain.redrawOpenGlScene = delegate() {
		glViewport(0, 0, sdmain.width, sdmain.height);
		glClearColor(0.5f, 0.5f, 0.5f, 1);
		glClear(glNVGClearFlags);

		{
			nvg.beginFrame(sdmain.width, sdmain.height); // begin rendering
			scope (exit)
				nvg.endFrame(); // and flush render queue on exit

			nvg.save();
			nvg.translate(canvasX, canvasY);
			nvg.scale(viewportWidth, viewportHeight);
			nvg.beginPath();
			nvg.rect(0, 0, 1, 1);
			nvg.fillColor = NVGColor(0.4, 0.4, 0.4);
			nvg.fill();
			double igrid = 1.0 / grid;
			nvg.beginPath();
			for (int i = 0; i <= grid; i++)
			{
				double step = i * igrid;
				nvg.moveTo(0, step);
				nvg.lineTo(1, step);

				nvg.moveTo(step, 0);
				nvg.lineTo(step, 1);
			}
			nvg.strokeColor = NVGColor(0.7, 0.7, 0.7);
			nvg.strokeWidth = 1 / viewportWidth;
			nvg.stroke();
			drawCanvas();
			nvg.restore();

			linesView.items = lines;
			linesView.padding = Padding(8, 4);
			linesView.width = min(300, canvasX);
			linesView.draw();

			nvg.fillColor = NVGColor.white;
			nvg.text(linesView.width + 8, 8 + nvg.textFontAscender, "X - Line");
			nvg.text(linesView.width + 8, 8 + nvg.textFontAscender * 2, "C - Cubic Bezier");
			nvg.text(linesView.width + 8, 8 + nvg.textFontAscender * 3, "V - Quadratic Bezier");
			nvg.text(linesView.width + 8 + 400, 8 + nvg.textFontAscender, "Q - Bigger Grid");
			nvg.text(linesView.width + 8 + 400, 8 + nvg.textFontAscender * 2, "W - Smaller Grid");
			nvg.text(linesView.width + 8 + 400, 8 + nvg.textFontAscender * 3, "Ctrl-S - Print to Console");
		}
	};

	bool mouseDown, mouseMoved, mouseHeld;
	bool ctrl;
	sdmain.eventLoop(0, // no pulse timer required
			delegate(KeyEvent event) {
				if (event.pressed)
				{
					if (event == "Q")
					{
						if (grid > 1)
						{
							grid >>= 1;
							sdmain.redrawOpenGlSceneNow();
						}
					}
					else if (event == "W")
					{
						if (grid < 32)
						{
							grid <<= 1;
							sdmain.redrawOpenGlSceneNow();
						}
					}
					else if (event == "Ctrl")
					{
						ctrl = true;
					}
					else if (event == "X")
					{
						editIndex = cast(int) lines.length;
						lines ~= Line(Line.Type.linear, [vec2(0, 0), vec2(1, 1)]);
						sdmain.redrawOpenGlSceneNow();
					}
					else if (event == "C")
					{
						editIndex = cast(int) lines.length;
						lines ~= Line(Line.Type.cubicBezier, [vec2(0, 0), vec2(1, 0), vec2(1, 1), vec2(0, 1)]);
						sdmain.redrawOpenGlSceneNow();
					}
					else if (event == "V")
					{
						editIndex = cast(int) lines.length;
						lines ~= Line(Line.Type.quadraticBezier, [vec2(0, 0), vec2(1, 0), vec2(1, 1)]);
						sdmain.redrawOpenGlSceneNow();
					}
					else if (event == "Ctrl-S")
					{
						import std.stdio;

						writeln(serializeLines);
					}
				}
				else
				{
					if (event == "Ctrl")
					{
						ctrl = false;
					}
				}
			}, delegate(MouseEvent event) {
				if (linesView.processMouse(event))
				{
					sdmain.redrawOpenGlSceneNow();
					return;
				}

				if (event.type == MouseEventType.buttonPressed)
				{
					mouseDown = true;
					mouseMoved = false;
					if (hold(globalToCanvasX(event.x), globalToCanvasY(event.y), ctrl))
					{
						mouseHeld = true;
						sdmain.redrawOpenGlSceneNow();
					}
				}
				else if (event.type == MouseEventType.buttonReleased)
				{
					mouseDown = false;
					if (!mouseHeld && !mouseMoved && click(globalToCanvasX(event.x),
						globalToCanvasY(event.y), ctrl))
						sdmain.redrawOpenGlSceneNow();
					mouseHeld = false;
				}
				else if (event.type == MouseEventType.motion)
				{
					mouseMoved = true;
					if (mouseDown)
					{
						if (drag(globalToCanvasX(event.x), globalToCanvasY(event.y),
							event.dx / viewportWidth, event.dy / viewportHeight))
							sdmain.redrawOpenGlSceneNow();
					}
				}
			});

	flushGui();
}
