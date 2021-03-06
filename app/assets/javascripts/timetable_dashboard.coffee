$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getTimetable: '/get-timetable'
    getTeacherTimetable: '/get-Ttimetable'
    getGroupTimetable: '/get-GroupTimetable '

  vm = ko.mapping.fromJS
    timetableList: []
    groups: []
    teacherName: ''
    groupNumber: ''

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')


  vm.getTable = ->
    $('#wait').show();
    $('#hide').hide();
    $.ajax
      url: apiUrl.getTimetable
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.timetableList response
      for k,v of vm.timetableList()
        $('#wait').hide();
        $('#hide').show();
        vm.groups.push k

  vm.getTable()

  vm.teacherName.subscribe (name) ->
    $('#wait').show();
    $('#hide').hide();
    vm.groups.removeAll()
    data =
      teacherName: name
    $.ajax
      url: apiUrl.getTeacherTimetable
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (result) ->
      vm.timetableList result
      for k,v of vm.timetableList()
        $('#wait').hide();
        $('#hide').show();
        vm.groups.push k


  vm.groupNumber.subscribe (number) ->
    $('#wait').show();
    $('#hide').hide();
    vm.groups.removeAll()
    data =
      groupNumber: number
    $.ajax
      url: apiUrl.getGroupTimetable
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (result) ->
      vm.timetableList result
      for k,v of vm.timetableList()
        $('#wait').hide();
        $('#hide').show();
        vm.groups.push k
#
#  $(document).ready ->
#  $('#search').on 'keyup', ->
#    value = $(this).val().toLowerCase()
#    $('#mydiv tr').filter ->
#      $(this).toggle $(this).text().toLowerCase().indexOf(value) > -1
#      if ($(this).text().toLowerCase().indexOf(value) > -1)
#        $(this).closest('tr').children('td');
#        console.log($(this).)


  ko.applyBindings {vm}