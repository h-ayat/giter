package giter.tiles

import p752.Tile
import p752.KeyEvent
import p752.EngineEvent
import p752.tiles.VerticalList
import p752.tiles.GenericList
import p752.KeyEvent.Key
import p752.KeyEvent.Special.Tab
import p752.KeyEvent.Special.Backspace
import p752.KeyEvent.Special.Enter
import p752.KeyEvent.Special.Up
import p752.KeyEvent.Special.Down
import p752.KeyEvent.Special.Del
import p752.KeyEvent.Special.ETB
import p752.tiles.GenericList.State

enum MenuItem(val char: Char):
  case Fetch extends MenuItem('F')
  case Update extends MenuItem('U')
  case Select extends MenuItem('S')
  case Terminate extends MenuItem('X')

private object Menu extends Tile[KeyEvent, Menu.State, Option[MenuItem]]:
  type State = GenericList.State[MenuItem]
  val defaultState = GenericList.State(MenuItem.values.toList)

  private def renderItem(in: MenuItem): String = in.char.toString + ":  " + in.toString()
  val list = new VerticalList[MenuItem](renderItem)

  override def render(state: State): String = list.render(state)

  override def update(
      event: KeyEvent,
      state: State
  ): (State, Option[MenuItem]) =
    event match
      case Key(ch) =>
        MenuItem.values.find(_.char == ch.toUpper) match
          case None => state -> None
          case Some(value) =>
            state -> Some(value)
        
      case _ =>
        val (s, e) = list.update(event, state)
        s -> (e match
          case None => None
          case Some(value) =>
            Some(value.value)
        )


