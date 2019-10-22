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
  $(document).on("click", ".edit", function () {
    var row = $(this).closest('tr').children('td');
    var fullName = row[1].innerText;
    var department = row[2].innerText;
    var tSubject = row[3].innerText;
    row[1].innerHTML = '<input type="text" class="form-control" value="' + fullName + '">';
    row[2].innerHTML = '<input type="text" class="form-control" value="' + department + '">';
    row[3].innerHTML = '<input type="text" class="form-control" value="' + tSubject + '">';
    $(this).parents("tr").find(".add, .edit").toggle();
  });
  $(document).on("click", ".add", function () {
    row = $(this).closest('tr').children('td');
    var data = {
      id: row[0].innerText,
      fullName: row[1].innerText,
      department: row[2].innerText,
      tSubject: row[3].innerText,
    };
    var postData = JSON.stringify(data);

    $.ajax({
      url: "/update/teacher",
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
      url: "/delete/teacher",
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