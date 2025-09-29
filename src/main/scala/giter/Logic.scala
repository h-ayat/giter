package giter

import sys.process._
import java.io.File
import scala.util.Using

object Logic:

  val (root, remoteName, exec, open, workdir) = {
    Using(
      scala.io.Source.fromFile(System.getenv("HOME") + "/.config/giter.conf")
    ) { l =>
      val map = l
        .getLines()
        .map(_.split("="))
        .filter(_.length == 2)
        .map(s => s(0) -> s(1))
        .toMap
      (map("root"), map("origin"), map("exec"), map("open"), map("workdir"))
    }
  }.get

  val remotePrefix = s"remotes/$remoteName/"

  def branchNames() =
    Process("git branch --all", new File(root)).!!.split("\n").toList
      .map(_.replace("+ ", "").replace("* ", "").trim)
      .filterNot(_.contains("->"))

  def treeBranches(): Set[Branch] =
    Process("git worktree list", new File(root)).!!.split("\n").toList
      .filter(_.contains('['))
      .map(_.dropWhile(_ != '[').tail.init)
      .filterNot(_ == "bare")
      .map(Branch(_, BranchMode.Tree))
      .toSet

  def localBranches(): Set[Branch] =
    branchNames()
      .filterNot(_.contains(remotePrefix))
      .map(_.replace(remotePrefix, "").trim)
      .map(Branch(_, BranchMode.Local))
      .toSet

  def remoteBranches(): Set[Branch] =
    branchNames()
      .filter(_.contains(remotePrefix))
      .map(_.replace(remotePrefix, "").trim)
      .map(Branch(_, BranchMode.Remote))
      .toSet

  private def toMap(set: Set[Branch]): Map[String, Branch] =
    set.map(a => a.name -> a).toMap

  def branches: Set[Branch] =

    val treeMap = toMap(treeBranches())
    val localMap = toMap(localBranches())
    val remoteMap = toMap(remoteBranches())
    val names = treeMap.keySet ++ localMap.keySet ++ remoteMap.keySet
    names.map(name =>
      treeMap.getOrElse(name, localMap.getOrElse(name, remoteMap(name)))
    )

  def fetch(): Unit =
    Process(
      Seq(
        "zsh",
        "-c",
        s"$open '$root' -e git fetch --all"
      )
    ).!<

  def open(branch: Branch): Unit =
    import branch.name
    val addr = workdir + "/" + name
    if branch.mode == BranchMode.Tree then
      val command = Seq(
        "setsid",
        "nohup",
        "bash",
        "-c",
        s"(setsid nohup $open '$addr' >/dev/null 2>&1 & disown) & disown"
      )
      val _ = Process(command).run()
    else
      val command0 = Seq(
        "zsh",
        "-c",
        s"$exec wm add $name"
      )
      Process(command0).run().exitValue()
      val command1 = Seq(
        "setsid",
        "nohup",
        "bash",
        "-c",
        s"(setsid nohup $open '$addr' >/dev/null 2>&1 & disown) & disown"
      )
      Process(command1).run()
  end open

end Logic
