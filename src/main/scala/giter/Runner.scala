package giter

import sys.process._
import p752.Engine
import giter.tiles.Main

object Runner:
  def main(args: Array[String]): Unit =
    val eng = new Engine(Main,Main.defaultState()) 
    eng.run()
    Thread.sleep(500)
    System.exit(0)
