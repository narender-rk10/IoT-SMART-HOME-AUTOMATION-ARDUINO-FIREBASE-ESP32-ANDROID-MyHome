const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const {dialogflow} = require('actions-on-google');
const {WebhookClient} = require('dialogflow-fulfillment');

var month = new Array();
month[0] = "January";
month[1] = "February";
month[2] = "March";
month[3] = "April";
month[4] = "May";
month[5] = "June";
month[6] = "July";
month[7] = "August";
month[8] = "September";
month[9] = "October";
month[10] = "November";
month[11] = "December";

function datetimeLocal(timezone ) {
    const localdate = new Date(Date.parse(new Date().toLocaleString('en-US', { timeZone: 'UTC' })) + (timezone * 90 * 60 * 1000));
    return localdate;
 }
 
var d = datetimeLocal(5);

exports.sendFireNotifications = functions.database.ref('users/3Oou95cKpPX6OPBduFNTOHx6sO32/fire/fireleak').onWrite((change, context) => {
	
	var data = change.after.val();
	
	if(data===0)
	{
		
	var deviceToken = admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/notification/nid`).once('value');
	return deviceToken.then(result => {
		var token_id = result.val();
		
		 const payload = {
			notification: {
            title: 'FIRE SENSOR',
            body: `ALERT! FIRE IS THERE IN YOUR HOME`,
            sound: "default",
			icon: "ic_launcher"
        }
		 };
		 
		const options = {
			priority: "high",
			timeToLive: 60 * 60 * 24
		};
    
		return admin.messaging().sendToDevice(token_id, payload,options );

	});
	}
	return null;

});

exports.sendGasNotifications = functions.database.ref("users/3Oou95cKpPX6OPBduFNTOHx6sO32/gas/gasleak").onWrite(( change,context) => {
   
	var data = change.after.val();
	
	if(data===1)
	{
	var deviceToken = admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/notification/nid`).once('value');

	return deviceToken.then(result => {
		
		var token_id = result.val();

		const payload = {
        notification: {
            title: 'GAS SENSOR',
            body: `ALERT! GAS LEAKAGE IS DETECTED IN YOUR HOME`,
            sound: "default",
			icon: "ic_launcher"
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };
    
	
	return admin.messaging().sendToDevice(token_id, payload,options );

	});
	}
	return null;

});

exports.sendPlantNotifications = functions.database.ref("users/3Oou95cKpPX6OPBduFNTOHx6sO32/plant/moisture").onWrite(( change,context) => {
   
	var data = change.after.val();
	var mp = (data / 1023.00) * 100;

if (data > 750) 
{

var deviceToken = admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/notification/nid`).once('value');

	return deviceToken.then(result => {
		
		 var token_id = result.val();
		 const payload = {
        notification: {
            title: 'PLANT MOINTORING',
            body: `ALERT! YOUR PLANT NEEDS WATER , CURRENT MOISTURE PERCENTAGE IS ${mp}`,
            sound: "default",
			icon: "ic_launcher"
        }
					};

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };
    	
	
	return admin.messaging().sendToDevice(token_id, payload,options );

	});
	}
	return null;

});


exports.sendWaterNotifications = functions.database.ref("users/3Oou95cKpPX6OPBduFNTOHx6sO32/water/level").onWrite(( change,context) => {
   
			var data = change.after.val();
			
			var dist;
			admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/water/distance`).on("value", function(snapshot) {
			dist = snapshot.val();
            });
			
                if (data === dist || data === dist-1) {  
				
				var deviceToken = admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/notification/nid`).once('value');

	return deviceToken.then(result => {
		
				var token_id = result.val();
				
		const payload = {
        notification: {
            title: 'WATER MOINTORING',
            body: `YOUR WATER LEVEL TANK IS FULL`,
            sound: "default",
			icon: "ic_launcher"
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };
    	
return admin.messaging().sendToDevice(token_id, payload,options );

	});
	}
	return null;

});
	
exports.receiveAssistantRequests = functions.https.onRequest((request, response) => {

  const agent = new WebhookClient({ request, response });
   //const app = new DialogflowApp({ request: request, response: response });

   function cc(agent) {

		const device = agent.parameters.devices;
        const status = agent.parameters.status;
		
			try{
        return admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/${device[0]}`).set(status[0]).then(snapshot => {
                return agent.add(`Ok, switching ${device} ${status}. Do you want to control anything else?`);
            });
			} catch (error) {
                return agent.add(`Soory Our Servers are not responding. Please try again later!`);
        }
    }
	
   function waterfn(agent) {
			try{
        return admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/water/level`).once("value").then(snapshot => {
			var wlval = snapshot.val();
                return agent.add(`Ok, water level is ${wlval} cm. Anything else?`);
            });
			} catch (error) {
                return agent.add(`Soory Our Servers are not responding. Please try again later!`);
        }
    }
	
	function plantfn(agent) {
			try{
        return admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/plant/moisture`).once("value").then(snapshot => {
			var plval = snapshot.val();
                return agent.add(`Ok, plant moisture is ${plval}. Anything else?`);
            });
			} catch (error) {
                return agent.add(`Soory Our Servers are not responding. Please try again later!`);
        }
    }

	function airfn(agent) {
			 try {
        return admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/air`).once("value").then(snapshot => {
			var atempval = snapshot.child("temp").val();
			var ahumidityval = snapshot.child("humidity").val();
			var amq135qval = snapshot.child("mq135q").val();
			var mq135Status;
			    if(amq135qval < 500){
                    mq135Status = "FRESH AIR";
                } else if(amq135qval > 500 && amq135qval <= 1000){
					mq135Status = "POOR AIR";
                } else if(amq135qval > 1000){
					mq135Status = "HAZARDOUS";
                }
                return agent.add(`Ok, Air Temperature is ${atempval} °C \n Air Humidity is ${ahumidityval} % \n Air Quality Index is ${amq135qval} PPM \n Air Quality: ${mq135Status}.\n Anything else?`);
            });
			} catch (error) {
                return agent.add(`Soory Our Servers are not responding. Please try again later!`);
        }
    }
	
	function electTodayUsage(agent) {
   
		var n = month[d.getMonth()];

		var dd = d.getDate();
		var mm = d.getMonth()+1; 
		var yyyy = d.getFullYear();
		if(dd<10) 
		{
		dd='0'+dd;
		} 

		if(mm<10) 
		{
		mm='0'+mm;
		} 

		var finalDate = yyyy+'-'+mm+'-'+dd;

  
		  return admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity`).once("value").then(snapshot => {
			 try {
			var etu_current = snapshot.child("current").child(n).child(finalDate).val();
			var etu_power = snapshot.child("power").child(n).child(finalDate).val();
			var etu_energyC = snapshot.child("power").child(n).child(finalDate).val();
			var etu_pf = snapshot.child("pf").val();
			var etu_frequency = snapshot.child("frequency").val();
			var etu_voltage = snapshot.child("voltage").val();
			var etu_0100 = snapshot.child("charges").child("0-100").val();
			var etu_101300 = snapshot.child("charges").child("101-300").val();
			var etu_301500 = snapshot.child("charges").child("301-500").val();
			var etu_5011000 = snapshot.child("charges").child("501-1000").val();
			var etu_M1000 = snapshot.child("charges").child("M1000").val();
			var etu_bill = 0;
			var etu_energy = etu_energyC/10;

			if(etu_energy!==null && etu_current!==null  && etu_power!==null && etu_voltage!==null && etu_frequency!==null && etu_pf!==null
                && etu_0100!==null && etu_101300!==null && etu_301500!==null && etu_5011000!==null && etu_M1000!==null)
                {
                  
					if (etu_energy < 100) {
                        etu_bill = etu_energy * etu_0100;
                    } else if (etu_energy >= 100 && etu_energy < 300) {
                        etu_bill = etu_energy * etu_101300;
                    } else if (etu_energy >=300 && etu_energy < 500) {
                        etu_bill = etu_energy * etu_301500;
                    } else if (etu_energy >=500 && etu_energy < 1000) {
                        etu_bill = etu_energy * etu_5011000;
                    } else if (etu_energy >= 1000) {
                        etu_bill = etu_energy * etu_M1000;
                    }

                return agent.add(`Ok, today's electricity usage ${n} ${finalDate} is \n BILL: ${etu_bill} ₹  \n ENERGY: ${etu_energy} kWh \n CURRENT: ${etu_current} A \n POWER: ${etu_power} W \n VOLTAGE: ${etu_voltage} V \n FREQUENCY: ${etu_frequency} Hz \n POWER FACTOR: ${etu_pf}. \n Anything else?`);
				}
				else{
                return agent.add(`Ok, today's electricity usage ${finalDate} NOT FOUND. \n Anything else?`);
				}
				} catch (error) {
                return agent.add(`Soory Our Servers are not responding. Please try again later!`);
        }
			});
    }
	
	function electThisMonthUsage(agent) {

		var n = month[d.getMonth()];

		try{
    return admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/`).once('value').then(snapshot => {
	
	var totalCurrent = 0;
    snapshot.child("current/"+n).forEach((childsnapshot)=> {       
         totalCurrent += childsnapshot.val();          
     }); 
	 
	var totalEnergyC = 0;
    snapshot.child("power/"+n).forEach((childsnapshot)=> {       
         totalEnergyC += childsnapshot.val();          
     }); 
	 
	var totalPower = 0;
    snapshot.child("power/"+n).forEach((childsnapshot)=> {       
         totalPower += childsnapshot.val();          
     }); 
	 
			var etu_0100 = snapshot.child("charges").child("0-100").val();
			var etu_101300 = snapshot.child("charges").child("101-300").val();
			var etu_301500 = snapshot.child("charges").child("301-500").val();
			var etu_5011000 = snapshot.child("charges").child("501-1000").val();
			var etu_M1000 = snapshot.child("charges").child("M1000").val();
			var etu_othercharges = snapshot.child("charges").child("othercharges").val();
			var etu_bill = 0;
			var totalEnergy = totalEnergyC/10;

			if(totalEnergy!==null && totalCurrent!==null  && totalPower!==null && etu_othercharges!==null && etu_0100!==null && etu_101300!==null && etu_301500!==null && etu_5011000!==null && etu_M1000!==null)
                {
                  
					if (totalEnergy < 100) {
                        etu_bill = totalEnergy * etu_0100;
                    } else if (totalEnergy >= 100 && totalEnergy < 300) {
                        etu_bill = totalEnergy * etu_101300;
                    } else if (totalEnergy >= 300 && totalEnergy < 500) {
                        etu_bill = totalEnergy * etu_301500;
                    } else if (totalEnergy >= 500 && totalEnergy < 1000) {
                        etu_bill = totalEnergy * etu_5011000;
                    } else if (totalEnergy >= 1000) {
                        etu_bill = totalEnergy * etu_M1000;
                    }
                return agent.add(`Ok, this month electricity usage ${n} is \n BILL: ${etu_bill} ₹  \n ENERGY: ${totalEnergy} kWh \n CURRENT: ${totalCurrent} A \n POWER: ${totalPower} W. \n Anything else?`);
				}
				else{
                return agent.add(`Ok, this month electricity usage ${n} NOT FOUND. \n Anything else?`);
				}
			});
			} catch (error) {
                return agent.add(`Soory Our Servers are not responding. Please try again later!`);
        }
    }
	
	function electLastMonthUsage(agent) {

		var n = month[d.getMonth()-1];

		try{
    return admin.database().ref(`users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/`).once('value').then(snapshot => {
	
	var totalCurrent = 0;
    snapshot.child("current/"+n).forEach((childsnapshot)=> {       
         totalCurrent += childsnapshot.val();          
     }); 
	 
	var totalEnergyC = 0;
    snapshot.child("power/"+n).forEach((childsnapshot)=> {       
         totalEnergyC += childsnapshot.val();          
     }); 
	 
	var totalPower = 0;
    snapshot.child("power/"+n).forEach((childsnapshot)=> {       
         totalPower += childsnapshot.val();          
     }); 
	 
			var etu_0100 = snapshot.child("charges").child("0-100").val();
			var etu_101300 = snapshot.child("charges").child("101-300").val();
			var etu_301500 = snapshot.child("charges").child("301-500").val();
			var etu_5011000 = snapshot.child("charges").child("501-1000").val();
			var etu_M1000 = snapshot.child("charges").child("M1000").val();
			var etu_othercharges = snapshot.child("charges").child("othercharges").val();
			var etu_bill = 0;
			var totalEnergy = totalEnergyC/10;

			if(totalEnergy!==null && totalCurrent!==null  && totalPower!==null && etu_othercharges!==null
			&& etu_0100!==null && etu_101300!==null && etu_301500!==null && etu_5011000!==null && etu_M1000!==null)
                {
					if (totalEnergy < 100) {
                        etu_bill = totalEnergy * etu_0100;
                    } else if (totalEnergy >= 100 && totalEnergy < 300) {
                        etu_bill = totalEnergy * etu_101300;
                    } else if (totalEnergy >= 300 && totalEnergy < 500) {
                        etu_bill = totalEnergy * etu_301500;
                    } else if (totalEnergy >= 500 && totalEnergy < 1000) {
                        etu_bill = totalEnergy * etu_5011000;
                    } else if (totalEnergy >= 1000) {
                        etu_bill = totalEnergy * etu_M1000;
                }
					
                return agent.add(`Ok, last month electricity usage ${n} is \n BILL: ${etu_bill} ₹  \n ENERGY: ${totalEnergy} kWh \n  CURRENT: ${totalCurrent} A \n POWER: ${totalPower} W. \n Anything else?`);
				}
				else{
					
                return agent.add(`Ok, last month electricity usage ${n} NOT FOUND. \n Anything else?`);
				
				}
			});
			} catch (error) {
                return agent.add(`Soory Our Servers are not responding. Please try again later!`);
        }
    }
	
	let intentMap= new Map();
	intentMap.set('Control_components', cc);  
	intentMap.set('water_level', waterfn); 
	intentMap.set('plant_status', plantfn); 
	intentMap.set('air_monitoring', airfn); 
	intentMap.set('today_usage', electTodayUsage); 
	intentMap.set('last_month_usage', electLastMonthUsage); 
	intentMap.set('this_month_usage', electThisMonthUsage); 
	agent.handleRequest(intentMap);
});