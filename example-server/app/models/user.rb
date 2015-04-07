class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  before_save :ensure_authentication_token
  belongs_to:post
  
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable, :token_authenticatable,
		 :confirmable
		 
  def skip_confirmation!
	self.confirmed_at = Time.now
  end
end
