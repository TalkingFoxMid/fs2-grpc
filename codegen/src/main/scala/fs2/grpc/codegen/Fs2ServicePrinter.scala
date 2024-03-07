package fs2.grpc.codegen

import scalapb.compiler.FunctionalPrinter

trait Fs2ServicePrinter {
  def printService(printer: FunctionalPrinter): FunctionalPrinter
}
