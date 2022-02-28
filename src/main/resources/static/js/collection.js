$(window).on("load",function(){
    $(".loader-wrapper").fadeOut("slow");
    document.getElementById('wrapper').style.display="block";
});

$(document).ready(function() {
    connect();
    var stompClient = null;
    document.getElementById('unreceived-size').innerHTML =  unreceivedReminders.length.toString();
});
document.getElementById('submit-password').addEventListener('click',editPassword);
function showMessage(reminder) {
    console.log(reminder);
    $.notify({
        icon: 'glyphicon glyphicon-bell',
        title:reminder.employee,
        message:  reminder.message + "<hr><center><a onclick='viewReminders'><b>view</b></a> </center>",
    },{
        type: 'minimalist',
        timer: 9000,
        placement: {
            from: 'top',
            align: 'right'
        },
        template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert">' +
            '<div class="row">'+
            '<div class="col-md-6">'+
            '<span data-notify="icon" class="fas fa-bell" style="font-size:20px"></span>'+
            '</div>'+
            '<div class="col-md-6">'+
            '<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>'+
            '</div>'+
            '</div>'+
            '<hr>'+
            '<div style="margin-top:10px" data-notify="message">'+
            '<span>'+ reminder.message+'</span>'+
            '</div>'+
            '</div>'
    });
    new Audio('/audio/pop.mp3').play();
}

function viewUnReceived(id,element){
    for(let i=0;i<unreceivedReminders.length;i++){
        if(unreceivedReminders[i].id === id){
            showMessage(unreceivedReminders[i]);
            element.remove();
            unreceivedReminders.splice(i, 1);
            document.getElementById('unreceived-size').innerHTML=unreceivedReminders.length.toString();
            fetch(`/admin/reinder/receceived/${id}`);
        }
    }
}

function editPassword(e){
    e.preventDefault();
    const newPassword =  document.getElementById('new-password');
    const currentPassword =  document.getElementById('current-password');
    const confirmPassword =  document.getElementById('confirm-password');
    if(newPassword.value === confirmPassword.value){
        const formData = new FormData();
        formData.append("newPassword",newPassword.value);
        formData.append("currentPassword",currentPassword.value);
        fetch("/api/hr/users/edit",{
            method:'POST',
            body:formData
        })
            .then(function(res){
                document.getElementById('lock-form').reset();
                $('#lock-modal').modal('hide');
                if(res.status == 409){
                    errorNotify("Incorrect Password");
                    throw new Error(res.statusText);
                }
                else if(!res.ok){
                    errorNotify("Something went Wrong");
                    throw new Error(res.statusText);
                }
                else{
                    successNotify("Password Editted");
                }
            })
    }
    else{
        alert("Passwords Dont match");
        newPassword.value = "";
        confirmPassword.value= "";
    }
}

function connect() {
    var socket = new SockJS('/our-websockets');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/reminders', function (reminder) {
            showMessage(JSON.parse(reminder.body));
        });
    });
}

function successNotify(message){
    $.notify({},{
        type: 'maximalise',
        timer: 5000,
        placement: {
            from: 'top',
            align: 'right'
        },
        template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert">' +
            '<div class="row">'+
            '<div class="col-md-3">'+
            '<span data-notify="icon" class="fas fa-check-circle"></span>'+
            '</div>'+
            '<div class="col-md-6">'+
            '<label style="color:#ffffff ">'+ message +'</label>'+
            '</div>'+
            '<div class="col-md-3">'+
            '<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>'+
            '</div>'+
            '</div>'+
            '</div>'
    });
}

function errorNotify(message){
    $.notify({},{
        type: 'errormalise',
        timer: 5000,
        placement: {
            from: 'top',
            align: 'right'
        },
        template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert">' +
            '<div class="row">'+
            '<div class="col-md-2">'+
            '<span data-notify="icon" class="fas fa-check-circle"></span>'+
            '</div>'+
            '<div class="col-md-8">'+
            '<label style="color:#ffffff ">'+ message +'</label>'+
            '</div>'+
            '<div class="col-md-2">'+
            '<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>'+
            '</div>'+
            '</div>'+
            '</div>'
    });
}