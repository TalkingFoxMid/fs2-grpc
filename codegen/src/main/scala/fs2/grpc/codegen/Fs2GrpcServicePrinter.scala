/*
 * Copyright (c) 2018 Gary Coady / Fs2 Grpc Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fs2.grpc.codegen

import com.google.protobuf.Descriptors.{MethodDescriptor, ServiceDescriptor}
import scalapb.compiler.FunctionalPrinter.PrinterEndo
import scalapb.compiler.{DescriptorImplicits, FunctionalPrinter, StreamType}

class Fs2GrpcServicePrinter(val service: ServiceDescriptor, val serviceSuffix: String, val di: DescriptorImplicits) extends Fs2AbstractServicePrinter {
  import fs2.grpc.codegen.Fs2AbstractServicePrinter.constants._
  import di._

  override protected def serviceMethodSignature(method: MethodDescriptor): String = {

    val scalaInType = method.inputType.scalaType
    val scalaOutType = method.outputType.scalaType
    val ctx = s"ctx: $Ctx"

    s"def ${method.name}" + (method.streamType match {
      case StreamType.Unary => s"(request: $scalaInType, $ctx): F[$scalaOutType]"
      case StreamType.ClientStreaming => s"(request: $Stream[F, $scalaInType], $ctx): F[$scalaOutType]"
      case StreamType.ServerStreaming => s"(request: $scalaInType, $ctx): $Stream[F, $scalaOutType]"
      case StreamType.Bidirectional => s"(request: $Stream[F, $scalaInType], $ctx): $Stream[F, $scalaOutType]"
    })
  }

  override protected def handleMethod(method: MethodDescriptor): String = {
    method.streamType match {
      case StreamType.Unary => "unaryToUnaryCall"
      case StreamType.ClientStreaming => "streamingToUnaryCall"
      case StreamType.ServerStreaming => "unaryToStreamingCall"
      case StreamType.Bidirectional => "streamingToStreamingCall"
    }
  }

}


object Fs2GrpcServicePrinter {

  private[codegen] object constants {

    private val effPkg = "_root_.cats.effect"
    private val fs2Pkg = "_root_.fs2"
    private val fs2grpcPkg = "_root_.fs2.grpc"
    private val grpcPkg = "_root_.io.grpc"

    // /

    val Ctx = "A"

    val Async = s"$effPkg.Async"
    val Resource = s"$effPkg.Resource"
    val Dispatcher = s"$effPkg.std.Dispatcher"
    val Stream = s"$fs2Pkg.Stream"

    val Fs2ServerCallHandler = s"$fs2grpcPkg.server.Fs2ServerCallHandler"
    val Fs2ClientCall = s"$fs2grpcPkg.client.Fs2ClientCall"
    val ClientOptions = s"$fs2grpcPkg.client.ClientOptions"
    val ServerOptions = s"$fs2grpcPkg.server.ServerOptions"
    val Companion = s"$fs2grpcPkg.GeneratedCompanion"

    val ServerServiceDefinition = s"$grpcPkg.ServerServiceDefinition"
    val Channel = s"$grpcPkg.Channel"
    val Metadata = s"$grpcPkg.Metadata"

  }

}
