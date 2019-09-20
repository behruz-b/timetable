
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
class groupDashboard @javax.inject.Inject() /*1.6*/(webJarsUtil: org.webjars.play.WebJarsUtil) extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*2.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {
/*3.2*/import views.html.main


Seq[Any](_display_(/*4.2*/main("TimeTable - Dashboard Groups", webJarsUtil)/*4.51*/ {_display_(Seq[Any](format.raw/*4.53*/("""
  """),format.raw/*5.3*/("""<script src=""""),_display_(/*5.17*/routes/*5.23*/.Assets.versioned("javascripts/groups.js")),format.raw/*5.65*/("""" type="text/javascript"></script>
  <div class="container">
    <div class="col-md-8 text-center offset-md-2">
      <br><br><br><br><br>
      <h1>Dashboard</h1>
      <table class="table table-hover table-condensed table-bordered">
        <thead style="background:linen;">
          <tr>
            <th>№</th>
            <th>Group Name</th>
            <th>Group`s Direction</th>
          </tr>
        </thead>
        <tbody class="" data-bind="foreach: vm.listGroups">
          <tr>
            <td data-bind="text: $data.id"></td>
            <td data-bind="text: $data.name"></td>
            <td data-bind="text: $data.direction"></td>
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
                  DATE: 2019-09-19T15:31:06.092
                  SOURCE: /home/prince/IdeaProjects/timetable/app/views/group_dashboard.scala.html
                  HASH: 2b7d470ab1f604122461ae0f97224fe0538b745d
                  MATRIX: 482->5|809->50|884->54|935->78|992->127|1031->129|1060->132|1100->146|1114->152|1176->194|1925->913
                  LINES: 19->1|22->2|25->3|28->4|28->4|28->4|29->5|29->5|29->5|29->5|52->28
                  -- GENERATED --
              */
          