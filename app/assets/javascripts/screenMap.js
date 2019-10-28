$(document).ready(function () {

  var element = document.getElementById("html-page");

  var getCanvas;
  setTimeout(function(){
    html2canvas(element, {
      onrendered: function (canvas) {
        // $("#previewImage").append(canvas);
        getCanvas = canvas;
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