package giter

enum BranchMode:
  case Tree, Local, Remote

case class Branch(name: String, mode: BranchMode)
