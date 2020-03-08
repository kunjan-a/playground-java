require 'net/http'
require 'json'
require 'mysql2'
require 'optparse'

def getOTP(email)
	client = Mysql2::Client.new(:host => "build-apps101.use1a.i.domain.co", :username => "zeus", :database => "DTABASE", :password => "PASSWORD")
	results = client.query("select code from entities e, otps o where o.entity_id = e.id and o.attributes = 0 and e.entity = '#{email}' order by entity_id desc limit 1")
	token = nil
	results.each do |row|
	  	token = row["code"]
	end
	return token
end

def main
    email = nil
    parser = OptionParser.new do |opts|
        opts.banner = "Get OTP for email."
        opts.define_head "Usage: ruby #{$0} [options]"

        opts.on("-e", "--email EMAIL", String,
                 "Email of the user") do |opt|
            email = opt
        end

    end
    parser.parse!

    if email
    	otp = getOTP(email)
		if otp.to_s.empty?
			puts "No unused OTP found"
		else
			puts otp
		end

    else
        $stderr.puts parser.help()
    end
end

main if __FILE__ == $0
