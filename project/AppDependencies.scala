import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.7.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"          %% "play-conditional-form-mapping-play-30"    % "3.2.0",
    "uk.gov.hmrc"          %% "bootstrap-frontend-play-30"               % bootstrapVersion,
    "org.typelevel"        %% "cats-core"                                % "2.12.0",
    "uk.gov.hmrc"          %% "play-frontend-hmrc-play-30"               % "11.11.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"              %% "scalatest"                % "3.2.19",
    "uk.gov.hmrc"                %% "bootstrap-test-play-30"   % bootstrapVersion,
    "org.mockito"                 % "mockito-core"             % "5.14.2",
    "org.scalatestplus"          %% "mockito-5-12"             % "3.2.19.0",
    "org.scalacheck"             %% "scalacheck"               % "1.18.1",
    "org.scalatestplus"          %% "scalacheck-1-18"          % "3.2.19.0",
    "io.github.wolfendale"       %% "scalacheck-gen-regexp"    % "1.1.0",
    "org.jsoup"                   % "jsoup"                    % "1.18.1",
  ).map(_ % "test")

  def apply(): Seq[ModuleID] = compile ++ test
}
