const fs = require('fs');

const data = `FRONT END,Afreen Ahmed,14-12-2003,10-05-2026
FRONT END,Vinaykumar Y D,03-06-2000,04-05-2026
FRONT END,JAMES J,14-09-2004,10-06-2026
FRONT END,Veeksha V Macheri,15-05-1996,01-07-2026
FRONT END,Sushmita Sudhakar,23-01-2000,16-06-2026
FRONT END,Sai Dhanush AR,08-02-2000,02-06-2026
FRONT END,Rahul,20-10-2002,16-06-2026
FRONT END,Sachin HK,23-08-2002,26-06-2026
FRONT END,Thejaswini HM,01-04-2003,13-07-2026
DEVOPS,Akarsh Gowda,11-04-2002,24-06-2026
DEVOPS,Akshay Gouda,14-07-1999,02-06-2026
DEVOPS,Anjali c,07-07-1998,18-05-2026
DEVOPS,Basavalingaiah B M,17-10-1998,02-06-2006
DEVOPS,Hemanth H Poojary,09-08-2002,15-06-2026
DEVOPS,J Praveen,11-07-1997,06-07-2026
DEVOPS,KirthickRaja N,21-05-2004,02-06-2026
DEVOPS,Likitha V,28-02-2001,15-06-2026
DEVOPS,Rakshith K,10-06-2003,13-07-2026
DEVOPS,Shine Das,10-05-1995,06-07-2026
DEVOPS,shravyashree P T,26-04-2000,02-06-2026
DEVOPS,Udaya Kumar G B,27-09-2002,13-07-2026
DEVOPS,Varun G,10-12-2000,26-05-2026
DEVOPS,Vishal M,29-03-2001,26-05-2026
DATA ANALYST,PRAJWAL R,16-04-2000,12-05-2026
DATA ANALYST,G.MOHITH KUMAR REDDY,15-04-2001,25-05-2026
DATA ANALYST,GOWTHAM B,25-04-2003,11-05-2026
DATA ANALYST,HARSHA SURESH NAIK,06-11-2001,06-07-2026
DATA ANALYST,ASHOK CHIPPE,02-06-2001,06-07-2026
DATA ANALYST,DHYAN M,24-11-2000,07-07-2026
DATA ANALYST,SURYA KALA RS,02-03-2001,07-07-2026
DATA ANALYST,SHASHANK,21-04-2001,01-06-2026
DATA ANALYST,FAIYAZ,18-10-2000,06-04-2026
DATA ANALYST,ABDUL,01-09-2003,07-04-2026
DATA ANALYST,AMRUTHESH KL,24-01-2002,07-07-2026
JAVA,Ganghadhar,10-07-2001,18-05-2026
JAVA,Nachiketh S,14-06-2001,07-04-2026
JAVA,Prajwal R,21-05-2002,01-04-2026
JAVA,akash,27-12-1996,26-05-2026
JAVA,Nitin Gowda,02-01-2000,15-06-2026
JAVA,Prince kumar Yadav,04-03-1999,24-05-2026
JAVA,Preethi P J,26-04-2001,18-05-2026
JAVA,Hussain,03-10-2001,15-06-2026
JAVA,Jitendra,19-03-2001,26-05-2026
JAVA,Shubham anand,22-10-1999,15-06-2026
JAVA,Chandana,17-01-2004,02-06-2026
JAVA,Geetha,02-08-2003,02-06-2026
JAVA,vivek,19-08-2004,13-07-2026
JAVA,Likitha,08-05-2005,13-07-2026
JAVA,Anaiza Anjum,09-12-2002,13-07-2026
JAVA,Rashmi S,28-02-1999,07-07-2026
JAVA,Shivakumar,04-04-1996,07-07-2026
JAVA,Aditya,31-10-2004,13-07-2026
JAVA,Vikas,13-10-2004,13-07-2026
JAVA,Yashodhar,07-06-2001,13-07-2026
JAVA,Ashuthosh kumar,08-01-2001,03-07-2026
PYTHON,SACHIN GOND,08-03-2002,07-07-2026
PYTHON,NAVEEN G,19-05-2000,12-07-2026
PYTHON,SHAMBHAVI GHORPADE,24-04-1998,07-07-2026
PYTHON,HARSHITHA S,25-08-2003,01-07-2026`;

const lines = data.trim().split(/\r?\n/);

const departments = {};
let dep_count = 1;
const users = [];
const employees = [];
let emp_count = 1;

const dept_map = {
    'FRONT END': 'FRONT_END',
    'DEVOPS': 'DEVOPS',
    'DATA ANALYST': 'DATA_ANALYST',
    'JAVA': 'JAVA',
    'PYTHON': 'PYTHON'
};

function convertDate(dStr) {
    const [day, month, year] = dStr.trim().split('-');
    return `${year}-${month}-${day}`;
}

lines.forEach(line => {
    if (!line.trim()) return;
    const [domain, name, dob_str, doj_str] = line.split(',');
    if (!departments[domain]) {
        departments[domain] = dep_count;
        dep_count++;
    }
    
    const dob = convertDate(dob_str);
    const doj = convertDate(doj_str);
    
    const parts = name.trim().split(' ');
    const first_name = parts[0].substring(0, 50).replace(/'/g, "''");
    const last_name = parts.length > 1 ? parts.slice(1).join(' ').substring(0, 50).replace(/'/g, "''") : '';
    const email = `emp${emp_count}@example.com`;
    const username = `emp${emp_count}`;
    
    users.push(`(NOW(), 'EMPLOYEE', '${username}', '${email}', 'password_hash', 'ACTIVE')`);
    
    const dept_id = departments[domain];
    employees.push(`(NOW(), 'EMP${emp_count.toString().padStart(3, '0')}', '${first_name}', '${last_name}', '${email}', '0000000000', 'MALE', '${dob}', '${doj}', 'FULL_TIME', ${dept_id}, ${dept_id}, ${dept_id}, ${emp_count}, 'EMPLOYEE')`);
    emp_count++;
});

let out = "";
out += "-- 1. Insert Departments\n";
out += "INSERT INTO departments (created_at, department_code, department_name, active) VALUES\n";
const dept_vals = [];
for (const k in departments) {
    dept_vals.push(`(NOW(), '${dept_map[k]}', '${k}', true)`);
}
out += dept_vals.join(',\n') + ";\n\n";

out += "-- 2. Insert Designations (Dummy based on Department)\n";
out += "INSERT INTO designations (created_at, designation_code, designation_name, department_id, active) VALUES\n";
const desig_vals = [];
for (const k in departments) {
    desig_vals.push(`(NOW(), 'DESIG_${dept_map[k]}', '${k} Engineer', ${departments[k]}, true)`);
}
out += desig_vals.join(',\n') + ";\n\n";

out += "-- 3. Insert Job Titles (Dummy based on Designation)\n";
out += "INSERT INTO job_titles (created_at, job_title_code, job_title, designation_id, active) VALUES\n";
const jt_vals = [];
for (const k in departments) {
    jt_vals.push(`(NOW(), 'JT_${dept_map[k]}', '${k} Developer', ${departments[k]}, true)`);
}
out += jt_vals.join(',\n') + ";\n\n";

out += "-- 4. Insert Users\n";
out += "INSERT INTO users (created_at, role, username, email, password, user_status) VALUES\n";
out += users.join(',\n') + ";\n\n";

out += "-- 5. Insert Employees\n";
out += "INSERT INTO employees (created_at, employee_code, first_name, last_name, email, phone_number, gender, date_of_birth, date_of_joining, employment_type, department_id, designation_id, job_title_id, user_id, role_name) VALUES\n";
out += employees.join(',\n') + ";\n";

fs.writeFileSync('C:/Users/User/.gemini/antigravity-ide/brain/16f75230-86e1-4423-b082-9c9dee1052cd/employee_inserts.md', "```sql\n" + out + "\n```");
