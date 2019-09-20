
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._

/**/
class subject_Dashboard @javax.inject.Inject() /*1.6*/(webJarsUtil: org.webjars.play.WebJarsUtil) extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*2.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {
/*3.2*/import views.html.main


Seq[Any](_display_(/*4.2*/main("TimeTable - Dashboard", webJarsUtil)/*4.44*/ {_display_(Seq[Any](format.raw/*4.46*/("""
"""),format.raw/*5.1*/("""<script src=""""),_display_(/*5.15*/routes/*5.21*/.Assets.versioned("javascripts/subjectDashboard.js")),format.raw/*5.73*/("""" type="text/javascript"></script>
  <div class="container">
    <div class="col-md-8 text-center offset-md-2">
      <br><br><br><br><br>
      <h1>Dashboard</h1>
      <table class="table table-hover table-condensed table-bordered">
        <thead style="background:linen;">
          <tr>
            <th>№</th>
            <th>Name</th>
            <th>Number Class Room</th>
          </tr>
        </thead>
        <tbody class="" data-bind="foreach: vm.subjectList">
          <tr>
            <td data-bind="text: $data.id"></td>
            <td data-bind="text: $data.name"></td>
            <td data-bind="text: $data.numberClassRoom"></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
""")))}),format.raw/*28.2*/("""
"""))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2019-09-20T13:43:08.070
                  SOURCE: /home/prince/IdeaProjects/timetable/app/views/subject_dashboard.scala.html
                  HASH: ec121697af214b213d18e20c032a131644a41592
                  MATRIX: 485->5|812->50|887->54|938->78|988->120|1027->122|1054->123|1094->137|1108->143|1180->195|1930->915
                  LINES: 19->1|22->2|25->3|28->4|28->4|28->4|29->5|29->5|29->5|29->5|52->28
                  -- GENERATED --
              */
          