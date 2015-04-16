class AddCategoryToSchedules < ActiveRecord::Migration
  def change
    	add_column :schedules, :category, :string
  end
end
