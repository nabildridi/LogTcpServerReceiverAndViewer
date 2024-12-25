

var source = new EventSource("/sse");
source.onmessage = (event) => {
	const obj = JSON.parse(event.data);
	refresh(obj);
};

const table = new DataTable('#logsDatatable', {
	order: [[0, 'desc']],
	columnDefs: [
		{
			target: 0,
			visible: false
		}
	],
	"createdRow": function(row, data, dataIndex) {
			$(row).addClass('table-'+color(data[3]));
	}
});

table.on('click', 'tbody tr', function() {
	let data = table.row(this).data();

	//alert('You clicked on ' + data[0] + "'s row");
	var logModal = new bootstrap.Modal($('#logModal'), {})
	$('#logLevel').addClass('alert-'+color(data[3]));
	$('#logLevel').text(data[3]);
	$('#logLogger').text(data[4]);
	$('#logMessage').text(data[5]);
	logModal.show();
});

function refresh(obj) {
	table.row
		.add([
			obj.timestamp,
			new Date(obj.timestamp).toLocaleString(),
			obj.remoteAddress,
			obj.level,
			obj.loggerName,
			obj.message
		])
		.draw(false);
}

function color(logLevel) {
	if (logLevel == `TRACE`) {
		return 'primary';
	}
	if (logLevel == `DEBUG`) {
		return 'success';
	}
	if (logLevel == `INFO`) {
		return 'info';
	}
	if (logLevel == `WARN`) {
		return 'warning';
	}
	if (logLevel == `ERROR`) {
		return 'danger';
	}
}

