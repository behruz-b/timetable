$(document).ready(function () {

  var element = document.getElementById("html-page");

  var getCanvas;
  setTimeout(function(){
    html2canvas(element, {
      onrendered: function (canvas) {
        // $("#previewImage").append(canvas);
        $("#img").attr("src", canvas.toDataURL("image/png").replace(
          /^data:image\/png/, "data:application/octet-stream"));
        getCanvas = canvas;
        var data = {
          img: canvas.toDataURL("image/png").replace(
            /^data:image\/png/, "data:application/octet-stream")
        };
        var postData = JSON.stringify(data);

        $.ajax({
          url: "/getImg",
          method: "POST",
          data: postData,
          contentType: "application/json",
          success: function (data) {
            alert(JSON.stringify(data));
          },
          error: function (errMsg) {
            alert(JSON.stringify(errMsg));
          }
        });
      }
    });
  }, 500);


  $("#btn-Convert-toImage").on('click', function () {
    var imageData =
      getCanvas.toDataURL("image/png");

    var newData = imageData.replace(
      /^data:image\/png/, "data:application/octet-stream");
    if (document.getElementById("floor").innerText === "1-qavat") {
      $("#btn-Convert-toImage").attr(
        "download", "FirstFloorMap.png").attr(
        "href", newData);
    }
    else if (document.getElementById("floor").innerText === "2-qavat") {
      $("#btn-Convert-toImage").attr(
        "download", "SecondFloorMap.png").attr(
        "href", newData);
    }
    else if (document.getElementById("floor").innerText === "3-qavat") {
      $("#btn-Convert-toImage").attr(
        "download", "ThirdFloorMap.png").attr(
        "href", newData);
    }

  });

});