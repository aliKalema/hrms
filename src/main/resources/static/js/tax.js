document.getElementById('add-nxt-rate').addEventListener('click',addRateItem);
document.getElementById('add-nxt-relief').addEventListener('click',addReliefItem);
document.getElementById('rate-submit').addEventListener('click',submitRate);
let tax={
    less:null,
    overBand:null,
    overRate:null,
    next:[],
    reliefs:[]
}
function addRateItem(){
    const rateList = document.getElementById('rate-list');
    const overRate = document.getElementById('over-rate');
    document.getElementById('add-nxt-rate').remove();
    overRate.remove();
    const li = document.createElement('li');
    li.className = 'rate-item';
    const output =`<div class="item-first">next</div>
	 	<div class="item-second">
	 	<input type="number" style="width:80%">
	 	</div>
	 	<div class="item-third">
	 	<input type="number"  style="width:100px; height:25px"><span>%</span>
	 	</div>
	 	<div class="item-fourth">
	 	<button id="add-nxt-rate" class="btn btn-primary btn-fab btn-fab-mini btn-round" style="float:right">
		  <i class="material-icons">arrow_downward</i>
		</button>
	 	</div>`;
    li.innerHTML = output;
    rateList.appendChild(li);
    rateList.appendChild(overRate);
    document.getElementById('add-nxt-rate').addEventListener('click',addRateItem);
}

function addReliefItem(){
    const reliefList = document.getElementById('relief-list');
    document.getElementById('add-nxt-relief').remove();
    const li = document.createElement('li');
    const output = `<div class="item-first">
                <div class="checkbox">
                    <label>
                        <input type="checkbox">
                        Everyone
                    </label>
                </div>
            </div>
			<div class="item-second">
			<input type="text">
			</div>
			<div class="item-third">
			<input type="number" style="width:100px;height:25px;">
			</div>
			<div class="item-fourth">
		 	<button id="add-nxt-relief" class="btn btn-primary btn-fab btn-fab-mini btn-round">
			  <i class="material-icons">arrow_downward</i>
			</button>
		 	</div>`;
    li.innerHTML = output;
    reliefList.appendChild(li);
    document.getElementById('add-nxt-relief').addEventListener('click',addReliefItem);
}

function getTax(){
    fetch("/api/admin/tax")
        .then(res =>res.json())
        .then(function(tax){
            const nextRanges =[];
            const rateUl = document.createElement('ul');
            document.getElementById('tax-wrapper-body').appendChild(rateUl);
            const less = document.createElement('li');
            less.className="tax-item";
            less.innerHTML =`<div class="tax-label">less than</div>
                            <div class="tax-band">${tax.less}</div>
                            <div class="tax-value">exempted</div>`;
            rateUl.appendChild(less);
            for(let j=2;j<tax.next.length+2;j++){
                for(let i=0;i<tax.next.length;i++){
                    if(tax.next[i].position === j){
                        let next = document.createElement('li');
                        next.className="tax-item";
                        next.innerHTML =`<div class="tax-label">next</div>
                                         <div class="tax-band">${tax.next[i].band}</div>
                                         <div class="tax-value unit-value">${tax.next[i].rate}%</div>`;
                        rateUl.appendChild(next);
                    }
                }
            }
            const over = document.createElement('li');
            over.className ='tax-item';
            over.innerHTML = `<div class="tax-label">over</div>
                              <div class="tax-band">${tax.overBand}</div>
                              <div class="tax-value unit-value">${tax.overRate}%</div>`;
            rateUl.appendChild(over);
            const reliefUl = document.createElement('ul');
            document.getElementById('relief-wrapper-body').appendChild(reliefUl);
            for(let i=0;i<tax.reliefs.length;i++){
                const relief = document.createElement('li');
                relief.className  = 'relief-item';
                relief.innerHTML= `<div class="relief-name">${tax.reliefs[i].name}</div>
                                   <div class="relief-name">${tax.reliefs[i].rate}</div>`;
                reliefUl.appendChild(relief);
            }
        });
}

function submitRate(e){
    e.preventDefault();
    const rateList = document.getElementById('rate-list').children;
    const reliefList = document.getElementById('relief-list').children;
    for(let i = 0;i<rateList.length;i++){
        if(i === 0){
            let band = rateList[i].children[1].children[0].value;
            tax.less=band;
        }
        else if(i === rateList.length-1){
            let band = rateList[i].children[1].children[0].value;
            let rate = rateList[i].children[2].children[0].value;
            tax.overBand=band;
            tax.overRate=rate;
        }
        else{
            let band = rateList[i].children[1].children[0].value;
            let rate = rateList[i].children[2].children[0].value;
            tax.next.push({band:band,rate:rate,position:i+1});
        }
    }
    for(let i =0;i<reliefList.length;i++){
        let everyOne= false;
        if(reliefList[i].children[0].children[0].children[0].children[0].checked){
            everyOne=  true;
        }
        let name = reliefList[i].children[1].children[0].value;
        let rate = reliefList[i].children[2].children[0].value;
        tax.reliefs.push({everyOne:everyOne,name:name,rate:rate})
    }
    const taxJson = JSON.stringify(tax);
    fetch('/api/hr/tax',{
        method:'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body:taxJson
    })
        .then(function(res){
            if(!res.ok){
                errorNotify('Something went Wrong');
                $('#add-tax-modal').modal('hide');
                throw Error(res.statusText);
            }
            return res;
        })
        .then(function(res){
            $('#add-tax-modal').modal('hide');
            swal({
                title: 'Tax',
                text: 'Tax Editted!',
                type: 'success',
                timer: 1500,
                confirmButtonClass: "btn btn-success",
                buttonsStyling: false
            })
        })
}
