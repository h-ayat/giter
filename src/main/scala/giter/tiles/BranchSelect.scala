package giter.tiles

import giter.Branch
import p752.Tile
import p752.KeyEvent
import p752.EngineEvent
import sys.process._

import p752.tiles.AutoComplete
import giter.Styles
import p752.tiles.AutoComplete.Event.Pass
import p752.tiles.AutoComplete.Event.ItemSelected
import p752.tiles.AutoComplete.Event.ItemMatched
import p752.tiles.AutoComplete
import giter.Logic
import giter.BranchMode
import p752.KeyEvent.Key
import p752.KeyEvent.Special.Tab
import p752.KeyEvent.Special.Backspace
import p752.KeyEvent.Special.Enter
import p752.KeyEvent.Special.Up
import p752.KeyEvent.Special.Down
import p752.KeyEvent.Special.Del
import p752.KeyEvent.Special.ETB

object BranchSelect
    extends Tile[KeyEvent, BranchSelect.State, BranchSelect.Result]:
  type State = AutoComplete.State[Branch]
  enum Result:
    case Change, Pass, Exit
  private val branches = Logic.branches.toList
  def defaultState(): State = AutoComplete.defaultState(branches)

  private def renderBranch(branch: Branch): String = 
    val prefix = branch.mode match
      case BranchMode.Tree => "   "
      case BranchMode.Local => "✔  "
      case BranchMode.Remote => "  "
    
    prefix + branch.name

  private val autoComplete = new AutoComplete[Branch](
    renderBranch,
    limit = 20,
    filter = (item, input) => item.contains(input)
  )

  override def render(state: State): String =
    Styles.Frames(autoComplete.render(state))

  override def update(event: KeyEvent, state: State): (State, Result) =
    event match
      case Tab =>
        state -> Result.Change
      case _ =>
        val (s, e) = autoComplete.update(event, state)
        s -> (e match
          case Pass => Result.Pass
          case ItemMatched(br) =>
            finish(br)
            Result.Exit
          case ItemSelected(br) =>
            finish(br)
            Result.Exit
        )

  private def finish(branch: Branch): Unit =
    Logic.open(branch)
