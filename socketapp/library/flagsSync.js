import flag from "../db/flags"
export default {
    flags=[],
    sync: function(socket){
        this.staticData();
        console.log("flagsync");
        socket.emit("syncflag",staticData);
    },
    staticData(){
        flags.push(new flag("234:1234:1234","orange","15"));
    }

}
