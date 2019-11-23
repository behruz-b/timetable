$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/send-timetable'
    getGroups: '/get-groups'
    getRooms: '/get-rooms'
    getSubject: '/get-subjects'
    getTeacherBySubject: '/get-tBySubject'

  vm = ko.mapping.fromJS
    selectedShift: ''
    studyShift: []
    selectedDay: ''
    weekDay: []
    selectedCouple: ''
    lCouple: []
    selectedType: ''
    typeLesson: []
    listGroups: []
    selectedGroup: ''
    listRooms: []
    selectedRoom: ''
    subjectList: []
    selectedSubject: ''
    listTeachers: []
    selectedTeacher: ''
    alternationType: []
    selectedAlternation:''
    multiValue: false
    alternationValue: false

  get = ->
    vm.studyShift([{id: 1, shift: "Morning"}, {id: 2, shift: "Afternoon"}])
    vm.weekDay([{id: 1, day: "Monday"}, {id: 2, day: "Tuesday"}, {id: 3, day: "Wednesday"}, {id: 4, day: "Thursday"},
      {id: 5, day: "Friday"}, {id: 6, day: "Saturday"}])
    vm.lCouple([{id: 1, couple: "couple 1"}, {id: 2, couple: "couple 2"}, {id: 3, couple: "couple 3"},
      {id: 4, couple: "couple 4"}])
    vm.typeLesson([{id: 1, type: "Laboratory"}, {id: 2, type: "Practice"}, {id: 3, type: "Lecture"}, {id: 4, type: "-"}])
    vm.alternationType([{id: 1, type: "even"}, {id: 2, type: "odd"}])

  get()

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText) or error.status is 200
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  getGroupList = ->
    $.ajax
      url:apiUrl.getGroups
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.listGroups(response)

  getGroupList()

  getRooms = ->
    $.ajax
      url: apiUrl.getRooms
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.listRooms(response)

  getRooms()

  getSubjectList = ->
    $.ajax
      url: apiUrl.getSubject
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.subjectList(response)

  getSubjectList()

  vm.selectedType.subscribe (type) ->
    vm.selectedType(type)

  $("#multi").change ->
    if (vm.alternationValue())
      $("#alternation").prop('checked', false);
    if (vm.multiValue())
      vm.multiValue false
      vm.alternationValue false
    else
      vm.multiValue true
      vm.alternationValue false

  $("#alternation").change ->
    if (vm.multiValue())
      $("#multi").prop('checked', false);
    if (vm.alternationValue())
      vm.multiValue false
      vm.alternationValue false
      vm.selectedAlternation undefined
    else
      vm.multiValue false
      vm.alternationValue true

  vm.selectedSubject.subscribe (subjectId) ->
    if subjectId is undefined
      data =
        tSubject: ""
    else
      vm.subjectList().map (pr) ->
        if pr.id is subjectId
          data =
            tSubject: pr.name
    $.ajax
      url: apiUrl.getTeacherBySubject
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      vm.listTeachers(response)

  vm.onSubmit = ->
    toastr.clear()
    console.log('alter: ', vm.selectedAlternation())
    if (!vm.selectedShift())
      toastr.error("Please select shift")
      return no
    else if (!vm.selectedDay())
      toastr.error("Please select day")
      return no
    else if (!vm.selectedCouple())
      toastr.error("Please select couple")
      return no
    else if (!vm.selectedSubject())
      toastr.error("Please select subject")
      return no
    else if (!vm.selectedType())
      toastr.error("Please select type")
      return no
    else if (!vm.selectedTeacher())
      toastr.error("Please select teacher")
      return no
    else if (!vm.selectedRoom())
      toastr.error("Please select room")
      return no
    else if (!vm.selectedGroup())
      toastr.error("Please select group")
      return no

    if (vm.selectedType() is "Laboratory")
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: vm.selectedGroup()
        divorce: 'half'
        subjectId: vm.selectedSubject()
        teachers: vm.selectedTeacher()
        numberRoom: vm.selectedRoom()
        flow: vm.multiValue()
        alternation: vm.selectedAlternation()


    else
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: vm.selectedGroup()
        divorce: ''
        subjectId: vm.selectedSubject()
        teachers: vm.selectedTeacher()
        numberRoom: vm.selectedRoom()
        flow: vm.multiValue()
        alternation: vm.selectedAlternation()

    $.ajax
      url: apiUrl.send
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)
      vm.selectedShift undefined
      vm.selectedDay undefined
      vm.selectedCouple undefined
      vm.selectedType undefined
      vm.selectedGroup undefined
      vm.selectedSubject undefined
      vm.selectedTeacher undefined
      vm.selectedRoom undefined
      vm.multiValue false
      vm.alternationValue false
      $("#multi").prop('checked', false);
      $("#alternation").prop('checked', false);
      vm.selectedAlternation undefined

  ko.applyBindings {vm}