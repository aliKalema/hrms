const months = ['JANUARY','FEBRUARY','MARCH','APRIL','MAY','JUNE','JULY','AUGUST','SEPTEMBER','OCTOBER','NOVEMBER','DECEMBER'];
let today = new Date();
let currentMonth;
let currentDate;
let currentYaer;
const selected = [];
const holidays = [];
createCalendar();
createDays(today);
function createCalendar(){
    currentDate = today;
    const calendarWrapper = document.createElement('div');
    calendarWrapper.className='calendar-wrapper';
    calendarWrapper.innerHTML=`<div class="year-month-wrapper">
                                    <div class="year-container">
                                        <div class="icon" onclick="previousYear()">
                                            <i class="fa fa-chevron-left" aria-hidden="true"></i>
                                        </div>
                                        <div class="year" id="year">
                                            ${currentDate.getFullYear()}
                                        </div>
                                        <div class="icon" onclick="nextYear()">
                                            <i class="fa fa-chevron-right" aria-hidden="true"></i>
                                        </div>
                                    </div>
                                    <div class="months-container">
                                        <div class="month-item" onclick="choseMonth(0)">
                                            JANUARY
                                        </div>
                                        <div class="month-item" onclick="choseMonth(1)">
                                            FEBRUARY
                                        </div>
                                        <div class="month-item" onclick="choseMonth(2)">
                                            MARTCH
                                        </div>
                                        <div class="month-item" onclick="choseMonth(3)">
                                            APRIL
                                        </div>
                                        <div class="month-item" onclick="choseMonth(4)">
                                            MAY
                                        </div>
                                        <div class="month-item" onclick="choseMonth(5)">
                                            JUNE
                                        </div>
                                        <div class="month-item" onclick="choseMonth(6)">
                                            JULY
                                        </div>
                                        <div class="month-item" onclick="choseMonth(7)">
                                            AUGUST
                                        </div>
                                        <div class="month-item" onclick="choseMonth(8)">
                                            SEPTEMBER
                                        </div>
                                        <div class="month-item" onclick="choseMonth(9)">
                                            OCTOBER
                                        </div>
                                        <div class="month-item" onclick="choseMonth(10)">
                                            NOVEMBER
                                        </div>
                                        <div class="month-item" onclick="choseMonth(11)">
                                            DECEMBER
                                        </div>
                                    </div>
                                </div>
                                <div class="calendar-container" id="calendar-container">
                                    <div class="days-title" id="day-title">
                                        <div class="day-item">
                                           SUN 
                                        </div>
                                        <div class="day-item">
                                            MON
                                        </div>
                                        <div class="day-item">
                                            TUE
                                        </div>
                                        <div class="day-item">
                                            WED
                                        </div>
                                        <div class="day-item">
                                            THU  
                                        </div>
                                        <div class="day-item">
                                            FRI
                                        </div>
                                        <div class="day-item">
                                            SAT
                                        </div>
                                    </div>
                                    <div class="days-container" id="days-container">

                                    </div>
                                </div>
                                <div class="description-container">
                                    <div class="description-heading">
                                        SELECTED
                                    </div>
                                    <div class="desciption-content"  id="selected-container">
                                    </div>

                                </div>`;
    document.getElementById('con').appendChild(calendarWrapper);
}

function createDays(date){
 const calendarContainer = document.getElementById('calendar-container');
 const daysContainer = document.getElementById('days-container');
 const day= date.getDay();
 const month = months[date.getMonth()];
 const year = date.getFullYear();
 const firstDay = new Date(date.getFullYear(), date.getMonth(), 1).getDay();
 const noOfDaysInMonth = new Date(date.getFullYear(), date.getMonth()+1, 0).getDate();
 for(let i = 0;i<firstDay;i++){
    let day = document.createElement('div');
    day.className="blank-day";
    daysContainer.appendChild(day);
 }
 for(let i =1;i<noOfDaysInMonth+1;i++){
        let day = document.createElement('div');
        day.className = "occupied-day";
        day.id = `${i}-${month}-${year}`;
        day.innerHTML = i;
        day.addEventListener('click',selectedDay)
        daysContainer.appendChild(day);
 }
}
function nextYear(){
    const year = Number(document.getElementById('year').innerHTML);
    document.getElementById('year').innerHTML = year +1;
}
function previousYear(){
const year = Number(document.getElementById('year').innerHTML);
document.getElementById('year').innerHTML = year -1;
}
function choseMonth(MonthIndex){
    const year = Number(document.getElementById('year').innerHTML);
    const date =  new Date(year,MonthIndex,1);
    document.getElementById('days-container').innerHTML = "";
    createDays(date);
}
function selectedDay(e){
    const day = e.target;
    selectedContainer = document.getElementById('selected-container');
    const date = day.id;
    const classes =(day.className);
    if(classes.length > 15){
        day.className="occupied-day";
        for(let sel of selectedContainer.children){
            if(sel.innerHTML == date){
                sel.remove();
            }
        }
    }
    else{
        
        day.className= "occupied-day selected-day";
        selected.push(date);
        let sel = document.createElement('div');
        sel.className ='selected-list';
        sel.innerHTML=`${date}`;
        selectedContainer.appendChild(sel);
    }
}
