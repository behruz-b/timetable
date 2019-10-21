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

  function getSubject() {
    $.ajax({
      url: "/get-subjects",
      type: 'GET',
      success: function (data) {
        subjects = data;
        console.log(subjects)
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
    row[1].innerHTML = '<input type="text" class="form-control" value="' + studyShift + '">';
    row[2].innerHTML = '<input type="text" class="form-control" value="' + weekday + '">';
    row[3].innerHTML = '<input type="text" class="form-control" value="' + couple + '">';
    row[4].innerHTML = '<input type="text" class="form-control" value="' + type + '">';
    row[5].innerHTML = '<input type="text" class="form-control" value="' + group + '">';
    row[6].innerHTML = '<input type="text" class="form-control" value="' + subject + '">';
    row[7].innerHTML = '<input type="text" class="form-control" value="' + teacher + '">';
    row[8].innerHTML = '<input type="text" class="form-control" value="' + numberRoom + '">';
    $(this).parents("tr").find(".add, .edit").toggle();
  });
  $(document).on("click", ".add", function () {
    row = $(this).closest('tr').children('td');

    subjects.forEach(function (el) {
      if (row[6].innerText === el.name) {
        subjectId = el.id;
        console.log(subjectId);
      }
    });

    var data = {
      id: row[0].innerText,
      studyShift: row[1].innerText,
      weekday: row[2].innerText,
      couple: row[3].innerText,
      type: row[4].innerText,
      group: row[5].innerText,
      subject: subjectId,
      teacher: row[7].innerText,
      numberRoom: row[8].innerText
    };
    console.log(data);
    var postData = JSON.stringify(data);

    $.ajax({
      url: "http://localhost:9000/update-timetable",
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
    $(this).parents("tr").remove();
  });
});