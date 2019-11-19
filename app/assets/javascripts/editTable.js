$(document).ready(function () {
  // Add row on add button click
  $(document).on("click", ".add", function () {
    var empty = false;
    var input = $(this).parents("tr").find('input[type="text"]');
    input.each(function () {
      if (!$(this).val()) {
        $(this).addClass("error");
        empty = true;
      } else {
        $(this).removeClass("error");
      }
    });
    $(this).parents("tr").find(".error").first().focus();
    if (!empty) {
      input.each(function () {
        $(this).parent("td").html($(this).val());
      });
      $(this).parents("tr").find(".add, .edit").toggle();
    }
  });
  // Edit row on edit button click
  var subjects = [];
  var subjectId;
  var flowB;
  var alter;

  function getSubject() {
    $.ajax({
      url: "/get-subjects",
      type: 'GET',
      success: function (data) {
        subjects = data;
      },
      error: function (errMsg) {
        alert(JSON.stringify(errMsg));
      }
    });
  }

  getSubject();
  $(document).on("click", ".edit", function () {
    var row = $(this).closest('tr').children('td');
    var studyShift = row[1].innerText;
    var weekday = row[2].innerText;
    var couple = row[3].innerText;
    var type = row[4].innerText;
    var group = row[5].innerText;
    var subject = row[6].innerText;
    var teacher = row[7].innerText;
    var numberRoom = row[8].innerText;
    var flow = row[9].innerText;
    var alternation = row[10].innerText;
    row[1].innerHTML = '<input type="text" class="form-control" value="' + studyShift + '">';
    row[2].innerHTML = '<input type="text" class="form-control" value="' + weekday + '">';
    row[3].innerHTML = '<input type="text" class="form-control" value="' + couple + '">';
    row[4].innerHTML = '<input type="text" class="form-control" value="' + type + '">';
    row[5].innerHTML = '<input type="text" class="form-control" value="' + group + '">';
    row[6].innerHTML = '<input type="text" class="form-control" value="' + subject + '">';
    row[7].innerHTML = '<input type="text" class="form-control" value="' + teacher + '">';
    row[8].innerHTML = '<input type="text" class="form-control" value="' + numberRoom + '">';
    row[9].innerHTML = '<input type="text" class="form-control" value="' + flow + '">';
    // if (alternation === "") {
    //   row[10].innerHTML = '<input type="text" class="form-control" value="No">';
    // } else {
      row[10].innerHTML = '<input type="text" class="form-control" value="' + alternation + '">';
    // }
    $(this).parents("tr").find(".add, .edit").toggle();
  });
  $(document).on("click", ".add", function () {
    var row = $(this).closest('tr').children('td');
    if (row[10].innerText === "even"){
      alter = row[10].innerText;
    } else if (row[10].innerText === "odd") {
      alter = row[10].innerText;
    } else {
      alter = undefined;
    }
    subjects.forEach(function (el) {
      if (row[6].innerText === el.name) {
        subjectId = el.id;
      }
    });
    if (row[9].innerText === "false"){
     flowB = false;
    } else {
      flowB = true;
    }


    var data = {
      id: row[0].innerText,
      studyShift: row[1].innerText,
      weekday: row[2].innerText,
      couple: row[3].innerText,
      type: row[4].innerText,
      group: row[5].innerText,
      subject: subjectId,
      teacher: row[7].innerText,
      numberRoom: row[8].innerText,
      flow: flowB,
      alternation: alter
    };
    console.log(data);
    var postData = JSON.stringify(data);

    $.ajax({
      url: "/update/timetable",
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
  });
  // Delete row on delete button click
  $(document).on("click", ".delete", function () {
    row = $(this).closest('tr').children('td');
    var data = {
      id: row[0].innerText,
    };
    var postData = JSON.stringify(data);
    $.ajax({
      url: "/delete/timetable",
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
    $(this).parents("tr").remove();
  });
});