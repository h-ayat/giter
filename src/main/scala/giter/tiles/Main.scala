package giter.tiles

import p752.EngineEvent
import p752.Tile
import p752.KeyEvent
import giter.tiles.Main.State.MenuState
import giter.tiles.Main.State.BranchState

object Main extends Tile[KeyEvent, Main.State, EngineEvent]:
  def defaultState(): State = State.BranchState(BranchSelect.defaultState())

  override def render(state: State): String = state match
    case MenuState(state) =>
      Menu.render(state)
    case BranchState(state) =>
      BranchSelect.render(state)

  override def update(event: KeyEvent, state: State): (State, EngineEvent) =
    state match
      case MenuState(s) =>
        val (newS, ress) = Menu.update(event, s)
        ress match
          case None => 
            State.MenuState(newS) -> EngineEvent.Pass
          case Some(value) =>
            value match
              case MenuItem.Fetch => ???
              case MenuItem.Update => ???
              case MenuItem.Select => 
                defaultState() -> EngineEvent.Pass
              case MenuItem.Terminate => 
                state -> EngineEvent.Terminate
            
      case BranchState(s) =>
        val (newS, res) = BranchSelect.update(event, s)
        res match
          case BranchSelect.Result.Change =>
            State.MenuState(Menu.defaultState) -> EngineEvent.Pass
          case BranchSelect.Result.Pass =>
            State.BranchState(newS) -> EngineEvent.Pass
          case BranchSelect.Result.Exit =>
            State.BranchState(newS) -> EngineEvent.Terminate

  sealed trait State
  object State:
    final case class MenuState(state: Menu.State) extends State
    final case class BranchState(state: BranchSelect.State) extends State
