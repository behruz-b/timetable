$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/send-teacher'
    getTeacher: '/get-teacher'


  vm = ko.mapping.fromJS
    fullName: ''
    tSubject: ''
    department: ''
    listTeachers: []

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.fullName())
      toastr.error("Please enter teacher's full name")
      return no
    else if (vm.fullName().length < 6)
      toastr.error("The teacher's full name must consist of 6 letters")
      return no
    else if (!vm.tSubject())
      toastr.error("Please enter teacher's  subject")
      return no
    else if (vm.tSubject().length < 6)
      toastr.error("The teacher's subject must consist of 6 letters")
      return no
    else if (!vm.department())
      toastr.error("Please enter teacher's department")
      return no
    else if (vm.department().length < 6)
      toastr.error("The teacher's department must consist of 6 letters")
      return no

    data =
      fullName: vm.fullName()
      tSubject: vm.tSubject()
      department: vm.department()

    $.ajax
      url: apiUrl.send
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)

  getTeachers = ->
    $.ajax
      url: apiUrl.getTeacher
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.listTeachers(response)

  getTeachers()

  ko.applyBindings {vm}