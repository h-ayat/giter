package giter

import sys.process._
import java.io.File
import scala.util.Using

object Logic:

  val (root, remoteName) = {
    Using(
      scala.io.Source.fromFile(System.getenv("HOME") + "/.config/giter.conf")
    ) { l =>
      val map = l.getLines()
        .map(_.split("="))
        .filter(_.length == 2)
        .map(s => s(0) -> s(1))
        .toMap
        map("root") -> map("origin")
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

  def open(branch: Branch): Unit = {
    if branch.mode == BranchMode.Tree then
      val addr = root + "/" + branch.name
      val command = Seq(
        "setsid",
        "nohup",
        "bash",
        "-c",
        s"(setsid nohup kitty --directory='$addr' >/dev/null 2>&1 & disown) & disown"
      )
      val _ = Process(command).run()
    else
      val name = branch.name
      val addr = root + "/" + name
      Process(
        Seq(
          "bash",
          "-c",
          s"cd $root && git worktree add $name o/$name && cd $name && git checkout $name"
        )
      ).!
      val command = Seq(
        "setsid",
        "nohup",
        "bash",
        "-c",
        s"(setsid nohup kitty --directory='$addr' >/dev/null 2>&1 & disown) & disown"
      )
      val _ = Process(command).run()

  }
end Logic
