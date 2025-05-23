import type {BodyPosition, Position} from "~/playground/data/common";

export class Segment {
  begin: Position
  end: Position

  constructor(begin: Position, end: Position) {
    this.begin = begin
    this.end = end
  }

  findPointWithX(targetX: number): Position | null {
    const x1 = this.begin.x
    const y1 = this.begin.y
    const x2 = this.end.x
    const y2 = this.end.y

    if ((targetX < x1 && targetX < x2) || (targetX > x1 && targetX > x2)) {
      return null
    }

    const y = y1 + ((targetX - x1) * (y2 - y1)) / (x2 - x1)
    return { x: targetX, y }
  }
}

export class Circle {
  center: Position
  radius: number

  constructor(center: Position, radius: number) {
    this.center = center
    this.radius = radius
  }
}

export class HalfCircle {
  center: Position
  radius: number
  angle: number

  constructor(center: Position, radius: number, angle: number) {
    this.center = center
    this.radius = radius
    this.angle = angle
  }

  chord(): Segment {
    const start: Position = {
      x: this.center.x + this.radius * Math.cos(this.angle),
      y: this.center.y + this.radius * Math.sin(this.angle)
    }

    const end: Position = {
      x: this.center.x - this.radius * Math.cos(this.angle),
      y: this.center.y - this.radius * Math.sin(this.angle)
    }

    return new Segment(start, end)
  }

  circle(): Circle {
    return new Circle(this.center, this.radius)
  }

  static of(bodyPosition: BodyPosition, radius: number): HalfCircle {
    return new HalfCircle({x: bodyPosition.x, y: bodyPosition.y}, radius, bodyPosition.angle)
  }
}
